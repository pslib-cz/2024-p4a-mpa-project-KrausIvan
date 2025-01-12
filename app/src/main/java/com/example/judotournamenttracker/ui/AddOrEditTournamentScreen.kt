package com.example.judotournamenttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Category
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.viewmodel.TournamentViewModel
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditTournamentScreen(
    navController: NavController,
    viewModel: TournamentViewModel,
    tournamentToEdit: Tournament?
) {
    val allCategories by viewModel.categories.collectAsState()
    var name by remember { mutableStateOf(tournamentToEdit?.name ?: "") }
    var location by remember { mutableStateOf(tournamentToEdit?.location ?: "") }
    var date by remember { mutableStateOf(tournamentToEdit?.date ?: "") }
    var description by remember { mutableStateOf(tournamentToEdit?.description ?: "") }

    var selectedCatIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    LaunchedEffect(tournamentToEdit) {
        if (tournamentToEdit != null) {
            val twc = runBlocking {
                viewModel.loadTournamentCategoriesOnce(tournamentToEdit.id)
            }
            val preSelected = twc?.categories
                ?.map { it.id }
                ?.toSet()
                ?: emptySet()
            selectedCatIds = preSelected
        }
    }

    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (tournamentToEdit == null) "Přidat turnaj" else "Upravit turnaj")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Název turnaje") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && name.isBlank()
            )
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Místo konání") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && location.isBlank()
            )
            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Datum (např. 1.8.2025)") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && date.isBlank()
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Popis") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && description.isBlank()
            )

            Text("Kategorie (můžete vybrat více):", style = MaterialTheme.typography.bodyLarge)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(allCategories) { cat ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(cat.name, modifier = Modifier.padding(end = 8.dp))
                        Checkbox(
                            checked = selectedCatIds.contains(cat.id),
                            onCheckedChange = { isChecked ->
                                selectedCatIds = if (isChecked) {
                                    selectedCatIds + cat.id
                                } else {
                                    selectedCatIds - cat.id
                                }
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (name.isBlank() || location.isBlank() || date.isBlank() || description.isBlank()) {
                        showError = true
                    } else {
                        showError = false
                        if (tournamentToEdit == null) {
                            viewModel.addTournamentMulti(
                                Tournament(
                                    name = name,
                                    location = location,
                                    date = date,
                                    description = description
                                ),
                                selectedCatIds
                            )
                        } else {
                            val updated = tournamentToEdit.copy(
                                name = name,
                                location = location,
                                date = date,
                                description = description
                            )
                            viewModel.updateTournamentMulti(updated, selectedCatIds)
                        }
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Uložit")
            }
            if (showError) {
                Text(
                    text = "Všechna pole (kromě kategorií) musí být vyplněna!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}