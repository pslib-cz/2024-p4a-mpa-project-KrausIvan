package com.example.judotournamenttracker.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.ui.AddCategoryScreen
import com.example.judotournamenttracker.ui.AddOrEditTournamentScreen
import com.example.judotournamenttracker.ui.CategoryListScreen
import com.example.judotournamenttracker.ui.TournamentDetailScreen
import com.example.judotournamenttracker.ui.TournamentListScreen
import com.example.judotournamenttracker.viewmodel.TournamentViewModel
import com.google.gson.Gson

@Composable
fun NavGraph(navController: NavHostController, viewModel: TournamentViewModel) {
    NavHost(
        navController = navController,
        startDestination = "tournament_list"
    ) {
        composable("tournament_list") {
            TournamentListScreen(navController, viewModel)
        }
        composable("add_tournament") {
            AddOrEditTournamentScreen(navController, viewModel, null)
        }
        composable(
            "tournament_detail/{tournament}",
            arguments = listOf(
                navArgument("tournament") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("tournament")
            val tourn = Gson().fromJson(json, Tournament::class.java)
            TournamentDetailScreen(navController, viewModel, tourn)
        }
        composable(
            "edit_tournament/{tournament}",
            arguments = listOf(
                navArgument("tournament") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("tournament")
            val tourn = Gson().fromJson(json, Tournament::class.java)
            AddOrEditTournamentScreen(navController, viewModel, tourn)
        }
        composable("category_list") {
            CategoryListScreen(navController, viewModel)
        }
        composable("add_category") {
            AddCategoryScreen(navController, viewModel)
        }
    }
}