package com.example.wowHub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wowHub.ui.screens.CharacterScreen
import com.example.wowHub.ui.screens.HomeScreen
import com.example.wowHub.ui.screens.WoWAuditRosterScreen
import com.example.wowHub.viewmodel.GuildRepository

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Roster : Screen("roster")
    object Character : Screen("character/{characterName}") {
        fun createRoute(characterName: String) = "character/$characterName"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: GuildRepository.GuildViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Roster.route) {
            WoWAuditRosterScreen(
                viewModel = viewModel,
                onCharacterClick = { characterName ->
                    navController.navigate(Screen.Character.createRoute(characterName))
                }
            )
        }
        composable(Screen.Character.route) { backStackEntry ->
            val characterName = backStackEntry.arguments?.getString("characterName") ?: return@composable
            CharacterScreen(
                characterName = characterName,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 