package com.example.judotournamenttracker.ui

import androidx.compose.foundation.layout.*
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
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    val oldCategoryId = remember(tournamentToEdit) {
        if (tournamentToEdit != null) {
            runBlocking {
                val twc = viewModel.getTournamentCategoriesFlow(tournamentToEdit.id).value
                twc?.categories?.firstOrNull()?.id
            }
        } else null
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
            Text("Kategorie (nepovinné)", style = MaterialTheme.typography.bodyLarge)
            CategoryDropdown(
                categories = allCategories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it }
            )
            Button(
                onClick = {
                    if (name.isBlank() || location.isBlank() || date.isBlank() || description.isBlank()) {
                        showError = true
                    } else {
                        showError = false
                        if (tournamentToEdit == null) {
                            viewModel.addTournament(
                                Tournament(
                                    name = name,
                                    location = location,
                                    date = date,
                                    description = description
                                ),
                                categoryId = selectedCategoryId
                            )
                        } else {
                            val updated = tournamentToEdit.copy(
                                name = name,
                                location = location,
                                date = date,
                                description = description
                            )
                            viewModel.updateTournament(
                                updated,
                                newCategoryId = selectedCategoryId,
                                oldCategoryId = oldCategoryId
                            )
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
                    text = "Všechna pole (kromě kategorie) musí být vyplněna!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = selectedCategory?.name ?: "Žádná vybraná kategorie")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Žádná") },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                }
            )
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.name) },
                    onClick = {
                        onCategorySelected(cat.id)
                        expanded = false
                    }
                )
            }
        }
    }
}