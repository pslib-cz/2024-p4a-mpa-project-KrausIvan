package com.example.judotournamenttracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.ui.TournamentListScreen
import com.example.judotournamenttracker.ui.AddTournamentScreen
import com.example.judotournamenttracker.ui.TournamentDetailScreen
import com.example.judotournamenttracker.viewmodel.TournamentViewModel
import com.google.gson.Gson

@Composable
fun NavGraph(navController: NavHostController, viewModel: TournamentViewModel) {
    NavHost(
        navController = navController,
        startDestination = "tournament_list"
    ) {
        composable("tournament_list") {
            TournamentListScreen(navController = navController, viewModel = viewModel)
        }
        composable("add_tournament") {
            AddTournamentScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "tournament_detail/{tournament}",
            arguments = listOf(
                navArgument("tournament") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tournamentJson = backStackEntry.arguments?.getString("tournament")
            val tournament = Gson().fromJson(tournamentJson, Tournament::class.java)
            TournamentDetailScreen(navController = navController, tournament = tournament)
        }
    }
}