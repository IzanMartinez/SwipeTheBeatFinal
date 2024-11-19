package com.izamaralv.swipethebeat.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object Main : Screen("main_screen")
}
