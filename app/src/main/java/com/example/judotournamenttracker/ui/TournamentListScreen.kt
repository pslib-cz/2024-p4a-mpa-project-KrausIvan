package com.example.judotournamenttracker.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Category
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.viewmodel.TournamentViewModel
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListScreen(
    navController: NavController,
    viewModel: TournamentViewModel,
    context: Context
) {
    val tournaments = viewModel.tournaments.collectAsState()
    val categories = viewModel.categories.collectAsState()
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Judo Závody") },
                actions = {
                    IconButton(onClick = {
                        exportDataToJson(context, tournaments.value)
                        Toast.makeText(context, "Data byla exportována", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Exportovat data")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_tournament") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Přidat závod")
                }
                FloatingActionButton(
                    onClick = { navController.navigate("add_category") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Přidat kategorii")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            DropdownMenuForCategories(categories.value, selectedCategory) {
                selectedCategory = it
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    if (selectedCategory != null) {
                        tournaments.value.filter { tournament ->
                            viewModel.isTournamentInCategory(tournament, selectedCategory!!)
                        }
                    } else {
                        tournaments.value
                    }
                ) { tournament ->
                    TournamentCard(tournament, navController)
                }
            }
        }
    }
}

@Composable
fun DropdownMenuForCategories(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true }) {
            Text(text = selectedCategory?.name ?: "Vyberte kategorii")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        expanded = false
                        onCategorySelected(category)
                    }
                )
            }
        }
    }
}

@Composable
fun TournamentCard(tournament: Tournament, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
            .clickable {
                val tournamentJson = Gson().toJson(tournament)
                navController.navigate("tournament_detail/$tournamentJson")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = tournament.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Klikni pro více informací",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

fun exportDataToJson(context: Context, tournaments: List<Tournament>) {
    val json = Gson().toJson(tournaments)
    val file = File(context.getExternalFilesDir(null), "tournaments.json")
    file.writeText(json)
}