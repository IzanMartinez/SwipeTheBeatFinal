package com.izamaralv.swipethebeat.navigation

// Clase sellada que define las rutas de navegación
sealed class Screen(val route: String) {
    data object Login : Screen("login_screen") // Ruta para la pantalla de inicio de sesión
    data object Main : Screen("main_screen") // Ruta para la pantalla principal
    data object LikedSongs : Screen("liked_songs_screen") // Ruta para la pantalla de canciones favoritas
    data object Profile : Screen("profile_screen") // Ruta para la pantalla de perfil
    data object ArtistPicker1 : Screen("artist_picker_1_screen") // Ruta para la pantalla de selección de artistas
    data object ArtistPicker2 : Screen("artist_picker_2_screen") // Ruta para la pantalla de selección de artistas
    data object ArtistPicker3 : Screen("artist_picker_3_screen") // Ruta para la pantalla de selección de artistas
    data object SavedSongs : Screen("saved_songs_screen") // Ruta para la pantalla de canciones guardadas
}

