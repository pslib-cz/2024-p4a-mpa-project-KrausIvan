package com.example.judotournamenttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.viewmodel.TournamentViewModel
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    navController: NavController,
    viewModel: TournamentViewModel,
    tournament: Tournament
) {
    val twc = remember(tournament.id) {
        runBlocking {
            viewModel.loadTournamentCategoriesOnce(tournament.id)
        }
    }
    val categories = twc?.categories ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail turnaje") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteTournament(tournament)
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Smazat turnaj")
                    }
                    IconButton(onClick = {
                        val json = Gson().toJson(tournament)
                        navController.navigate("edit_tournament/$json")
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Upravit turnaj")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Název: ${tournament.name}", style = MaterialTheme.typography.titleLarge)
            Text("Místo: ${tournament.location}")
            Text("Datum: ${tournament.date}")
            Text("Popis: ${tournament.description}")

            if (categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Kategorie:", style = MaterialTheme.typography.titleMedium)
                categories.forEach { cat ->
                    Text("- ${cat.name}")
                }
            }
        }
    }
}