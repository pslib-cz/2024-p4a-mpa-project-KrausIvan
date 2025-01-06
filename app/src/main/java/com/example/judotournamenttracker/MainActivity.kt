package com.example.judotournamenttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.judotournamenttracker.data.AppDatabase
import com.example.judotournamenttracker.navigation.NavGraph
import com.example.judotournamenttracker.ui.theme.JudoTournamentTrackerTheme
import com.example.judotournamenttracker.viewmodel.TournamentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializace databáze
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "judo_tournament_db" // Název databáze
        ).build()

        // Získání DAO z databáze
        val tournamentDao = database.tournamentDao()

        // Inicializace ViewModelu
        val viewModel = TournamentViewModel(tournamentDao)

        // Nastavení obsahu aplikace
        setContent {
            JudoTournamentTrackerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JudoTournamentTrackerTheme {
        Greeting("Android")
    }
}