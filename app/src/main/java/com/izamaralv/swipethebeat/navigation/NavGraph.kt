package com.izamaralv.swipethebeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.izamaralv.swipethebeat.screens.*
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel

@Composable
fun NavGraph(navController: NavHostController, profileViewModel: ProfileViewModel, searchViewModel: SearchViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController, profileViewModel = profileViewModel)
        }
        composable(route = Screen.LikedSongs.route) {
            LikedSongsScreen(navController = navController, profileViewModel = profileViewModel)
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                searchViewModel = searchViewModel // ✅ Pass SearchViewModel
            )
        }
    }
}
