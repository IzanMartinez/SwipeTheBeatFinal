package com.izamaralv.swipethebeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.izamaralv.swipethebeat.screens.LoginScreen
import com.izamaralv.swipethebeat.screens.MainScreen
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@Composable
fun NavGraph(navController: NavHostController, profileViewModel: ProfileViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController, profileViewModel = profileViewModel)
        }
    }
}
