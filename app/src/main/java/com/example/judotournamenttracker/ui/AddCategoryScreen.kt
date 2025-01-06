package com.example.judotournamenttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Category
import com.example.judotournamenttracker.viewmodel.TournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(navController: NavController, viewModel: TournamentViewModel) {
    var categoryName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Přidat kategorii") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Název kategorie") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && categoryName.isBlank()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (categoryName.isBlank()) {
                        showError = true
                    } else {
                        showError = false
                        viewModel.addCategory(Category(name = categoryName))
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
                    text = "Název kategorie nesmí být prázdný!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}