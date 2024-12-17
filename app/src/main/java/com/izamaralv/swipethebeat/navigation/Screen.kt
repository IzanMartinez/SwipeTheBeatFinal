package com.izamaralv.swipethebeat.navigation

// Clase sellada que define las rutas de navegación
sealed class Screen(val route: String) {
    data object Login : Screen("login_screen") // Ruta para la pantalla de inicio de sesión
    data object Main : Screen("main_screen") // Ruta para la pantalla principal
    data object LikedSongs : Screen("liked_songs_screen") // Ruta para la pantalla de canciones favoritas
}

