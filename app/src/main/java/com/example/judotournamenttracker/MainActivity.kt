package com.example.judotournamenttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.judotournamenttracker.data.AppDatabase
import com.example.judotournamenttracker.navigation.NavGraph
import com.example.judotournamenttracker.ui.theme.JudoTournamentTrackerTheme
import com.example.judotournamenttracker.viewmodel.TournamentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "judo_tournament_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val dao = db.tournamentDao()

        CoroutineScope(Dispatchers.IO).launch {
            if (dao.getAllTournamentsCount() == 0) {
                dao.insertTournamentReturnId(
                    com.example.judotournamenttracker.data.Tournament(
                        name = "Český pohár Jablonec",
                        location = "Jablonec nad Nisou",
                        date = "1.6.2025",
                        description = "Velká cena Brna"
                    )
                )
                dao.insertTournamentReturnId(
                    com.example.judotournamenttracker.data.Tournament(
                        name = "Velká cena Brna",
                        location = "Brno",
                        date = "1.7.2025",
                        description = "Velká cena Brna"
                    )
                )
                dao.insertTournamentReturnId(
                    com.example.judotournamenttracker.data.Tournament(
                        name = "Pražský pohár",
                        location = "Praha",
                        date = "1.8.2025",
                        description = "Velká cena Brna"
                    )
                )
            }
        }

        val viewModel = TournamentViewModel(dao)

        setContent {
            JudoTournamentTrackerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}