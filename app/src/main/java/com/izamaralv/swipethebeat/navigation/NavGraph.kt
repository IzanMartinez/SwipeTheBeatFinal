package com.izamaralv.swipethebeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.izamaralv.swipethebeat.screens.ArtistPickerScreen
import com.izamaralv.swipethebeat.screens.LikedSongsScreen
import com.izamaralv.swipethebeat.screens.LobbyScreen
import com.izamaralv.swipethebeat.screens.LoginScreen
import com.izamaralv.swipethebeat.screens.MainScreen
import com.izamaralv.swipethebeat.screens.ProfileScreen
import com.izamaralv.swipethebeat.screens.SavedSongsScreen
import com.izamaralv.swipethebeat.viewmodel.GeminiRecommendationViewModel
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel

/**
 * Define las rutas y pantallas de la aplicación.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel,
    songViewModel: SongViewModel,
    geminiViewModel: GeminiRecommendationViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        // Pantalla de login
        composable(Screen.Login.route) {
            LoginScreen()
        }

        // Pantalla principal con recomendaciones
        composable(Screen.Main.route) {
            MainScreen(
                navController     = navController,
                profileViewModel  = profileViewModel,
                geminiViewModel   = geminiViewModel
            )
        }

        // Lista de canciones marcadas con “Me gusta”
        composable(Screen.LikedSongs.route) {
            LikedSongsScreen(
                navController     = navController,
                profileViewModel  = profileViewModel
            )
        }

        // Perfil del usuario
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController     = navController,
                profileViewModel  = profileViewModel
            )
        }

        // Selección del artista favorito (slot 1)
        composable(Screen.ArtistPicker1.route) {
            val current = profileViewModel.favoriteArtist1
            val exclude = listOf(
                profileViewModel.favoriteArtist2,
                profileViewModel.favoriteArtist3
            )
            ArtistPickerScreen(searchViewModel, current, exclude) { chosen ->
                profileViewModel.changeFavoriteArtist(0, chosen)
                navController.popBackStack()
            }
        }

        // Selección del artista favorito (slot 2)
        composable(Screen.ArtistPicker2.route) {
            val current = profileViewModel.favoriteArtist2
            val exclude = listOf(
                profileViewModel.favoriteArtist1,
                profileViewModel.favoriteArtist3
            )
            ArtistPickerScreen(searchViewModel, current, exclude) { chosen ->
                profileViewModel.changeFavoriteArtist(1, chosen)
                navController.popBackStack()
            }
        }

        // Selección del artista favorito (slot 3)
        composable(Screen.ArtistPicker3.route) {
            val current = profileViewModel.favoriteArtist3
            val exclude = listOf(
                profileViewModel.favoriteArtist1,
                profileViewModel.favoriteArtist2
            )
            ArtistPickerScreen(searchViewModel, current, exclude) { chosen ->
                profileViewModel.changeFavoriteArtist(2, chosen)
                navController.popBackStack()
            }
        }

        // Canciones guardadas para más tarde
        composable(Screen.SavedSongs.route) {
            SavedSongsScreen(
                navController     = navController,
                profileViewModel  = profileViewModel
            )
        }

        // Pantalla de lobby o bienvenida
        composable(Screen.Lobby.route) {
            LobbyScreen(
                navController     = navController,
                profileViewModel  = profileViewModel
            )
        }
    }
}
