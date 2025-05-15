package com.izamaralv.swipethebeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.izamaralv.swipethebeat.screens.*
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@Composable
fun NavGraph(navController: NavHostController, profileViewModel: ProfileViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            // Pantalla de inicio de sesión
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Main.route) {
            // Pantalla principal
            MainScreen(navController = navController, profileViewModel = profileViewModel)
        }
        composable(route = Screen.LikedSongs.route) {
            // Pantalla de canciones favoritas
            LikedSongsScreen(navController = navController, profileViewModel = profileViewModel)
        }
        composable(route = Screen.Profile.route) {
            // Pantalla de perfil
            ProfileScreen(navController = navController, profileViewModel = profileViewModel)
        }
    }
}

