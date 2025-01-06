package com.example.judotournamenttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.viewmodel.TournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentScreen(
    navController: NavController,
    viewModel: TournamentViewModel
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Přidat závod") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Název") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && name.isBlank()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lokace") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && location.isBlank()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = date,
                onValueChange = { input ->
                    date = input.replace("[^\\d.]".toRegex(), "")
                },
                label = { Text("Datum (DD.MM.RRRR)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = showError && !isValidDate(date)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Popis") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && description.isBlank()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isBlank() || location.isBlank() || !isValidDate(date) || description.isBlank()) {
                        showError = true
                    } else {
                        showError = false
                        viewModel.addTournament(
                            Tournament(
                                name = name,
                                location = location,
                                date = date,
                                description = description
                            )
                        )
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Uložit")
            }

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vyplňte všechna pole a zadejte platné datum ve formátu DD.MM.RRRR!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun isValidDate(date: String): Boolean {
    val regex = Regex("""^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.(\d{4})$""")
    return regex.matches(date)
}