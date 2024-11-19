package com.izamaralv.swipethebeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.izamaralv.swipethebeat.screens.LoginScreen
import com.izamaralv.swipethebeat.screens.MainScreen
import com.izamaralv.swipethebeat.viewmodel.ProfileVIewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel

@Composable
fun NavGraph(navController: NavHostController, profileViewModel: ProfileVIewModel, songViewModel: SongViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController, profileViewModel = profileViewModel, songViewModel = songViewModel)
        }
    }
}
