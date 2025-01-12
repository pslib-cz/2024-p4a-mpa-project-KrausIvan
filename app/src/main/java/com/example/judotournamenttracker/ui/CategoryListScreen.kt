package com.example.judotournamenttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Category
import com.example.judotournamenttracker.viewmodel.TournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavController, viewModel: TournamentViewModel) {
    val allCategories by viewModel.categories.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newCatName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seznam kategorií") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Přidat kategorii")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCategories) { cat ->
                CategoryItem(cat) {
                    viewModel.deleteCategory(cat)
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nová kategorie") },
            text = {
                TextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Název kategorie") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCatName.isNotBlank()) {
                        viewModel.addCategory(Category(name = newCatName))
                    }
                    newCatName = ""
                    showDialog = false
                }) {
                    Text("Přidat")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    newCatName = ""
                    showDialog = false
                }) {
                    Text("Zrušit")
                }
            }
        )
    }
}

@Composable
fun CategoryItem(category: Category, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category.name, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Smazat kategorii")
            }
        }
    }
}