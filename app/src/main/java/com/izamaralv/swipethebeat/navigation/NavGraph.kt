package com.izamaralv.swipethebeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.izamaralv.swipethebeat.screens.ArtistPickerScreen
import com.izamaralv.swipethebeat.screens.LikedSongsScreen
import com.izamaralv.swipethebeat.screens.LoginScreen
import com.izamaralv.swipethebeat.screens.MainScreen
import com.izamaralv.swipethebeat.screens.ProfileScreen
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel,
    songViewModel: SongViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController, profileViewModel = profileViewModel, songViewModel = songViewModel)
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
        composable(Screen.ArtistPicker1.route) {
            ArtistPickerScreen(searchViewModel) { chosen ->
                profileViewModel.changeFavoriteArtist(0, chosen)
                navController.popBackStack()
            }
        }
        composable(Screen.ArtistPicker2.route) {
            ArtistPickerScreen(searchViewModel) { chosen ->
                profileViewModel.changeFavoriteArtist(1, chosen)
                navController.popBackStack()
            }
        }
        composable(Screen.ArtistPicker3.route) {
            ArtistPickerScreen(searchViewModel) { chosen ->
                profileViewModel.changeFavoriteArtist(2, chosen)
                navController.popBackStack()
            }
        }

    }
}
