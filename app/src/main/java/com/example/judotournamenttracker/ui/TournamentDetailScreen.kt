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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    navController: NavController,
    viewModel: TournamentViewModel,
    tournament: Tournament
) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Název: ${tournament.name}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Místo: ${tournament.location}")
            Text(text = "Datum: ${tournament.date}")
            Text(text = "Popis: ${tournament.description}")
        }
    }
}