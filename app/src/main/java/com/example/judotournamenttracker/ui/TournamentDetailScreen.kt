package com.example.judotournamenttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Tournament

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(navController: NavController, tournament: Tournament) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail turnaje") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Název: ${tournament.name}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Lokace: ${tournament.location}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Datum: ${tournament.date}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Popis: ${tournament.description}")
        }
    }
}