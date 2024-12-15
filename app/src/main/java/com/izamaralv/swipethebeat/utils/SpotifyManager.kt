package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.util.Log
import androidx.navigation.NavHostController
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.izamaralv.swipethebeat.viewmodel.SongViewModel
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.viewmodel.SongViewModelFactory

class SpotifyManager(private val context: Context) {

    private lateinit var songViewModel: SongViewModel

    fun initializeSpotifyClient(accessToken: String, refreshToken: String, expiresIn: Int) {
        Log.d("SpotifyManager", "Initializing SpotifyClient with accessToken = $accessToken, refreshToken = $refreshToken, expiresIn = $expiresIn")
        SpotifyClient.initialize(accessToken, refreshToken, expiresIn)
        SpotifyClient.forceTokenRefresh() // Force refresh to verify token handling
        Log.d("SpotifyManager", "SpotifyClient initialized")
    }

    fun initializeSongRepository(accessToken: String) {
        Log.d("SpotifyManager", "Initializing SongRepository with accessToken = $accessToken")
        val songRepository = SongRepository(context)
        val songViewModelFactory = SongViewModelFactory(songRepository, accessToken)
        songViewModel = songViewModelFactory.create(SongViewModel::class.java)
        Log.d("SpotifyManager", "SongRepository and SongViewModel initialized")
    }

    fun exchangeCodeForToken(code: String, onTokenReceived: (accessToken: String, refreshToken: String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val tokens = SpotifyApi.exchangeCodeForToken(code)
            tokens?.let { (accessToken, refreshToken) ->
                val tokenManager = TokenManager(context)
                tokenManager.saveTokens(accessToken, refreshToken)
                onTokenReceived(accessToken, refreshToken)
            } ?: run {
                Log.e("SpotifyManager", "Token exchange failed, tokens are null")
            }
        }
    }

    fun getAuthorizationUrl(clientId: String, redirectUri: String): String {
        val scopes = listOf(
            SpotifyScope.USER_LIBRARY_READ,
            SpotifyScope.USER_READ_PRIVATE,
            SpotifyScope.USER_READ_EMAIL,
            SpotifyScope.USER_TOP_READ,
            SpotifyScope.USER_FOLLOW_READ,
            SpotifyScope.USER_LIBRARY_MODIFY,
            SpotifyScope.PLAYLIST_READ_PRIVATE,
            SpotifyScope.PLAYLIST_READ_COLLABORATIVE,
            SpotifyScope.PLAYLIST_MODIFY_PRIVATE,
            SpotifyScope.PLAYLIST_MODIFY_PUBLIC,
            SpotifyScope.USER_READ_RECENTLY_PLAYED
        )

        val authorizationUrl = getSpotifyAuthorizationUrl(
            *scopes.toTypedArray(),
            clientId = clientId,
            redirectUri = redirectUri
        )

        return authorizationUrl
    }


    fun getSongViewModel(): SongViewModel {
        return songViewModel
    }

    fun logout(navController: NavHostController) {
        val tokenManager = TokenManager(context)
        tokenManager.clearTokens()
        navController.navigate("login_screen") {
            popUpTo("main_screen") { inclusive = true }
        }
    }
}
