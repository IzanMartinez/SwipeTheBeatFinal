package com.izamaralv.swipethebeat.utils

import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyClientApi
import kotlinx.coroutines.runBlocking
import com.izamaralv.swipethebeat.utils.Credentials.CLIENT_ID
import com.izamaralv.swipethebeat.utils.Credentials.CLIENT_SECRET
import com.izamaralv.swipethebeat.utils.Credentials.REDIRECT_URI

object SpotifyClient {
    lateinit var spotifyApi: SpotifyClientApi

    fun initialize(accessToken: String, refreshToken: String, expiresIn: Int) {
        runBlocking {
            try {
                Log.d("SpotifyClient", "Initializing Spotify API...")
                val token = Token(accessToken, refreshToken, expiresIn)
                spotifyApi = spotifyClientApi(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, token) {
                    automaticRefresh = true
                    onTokenRefresh = {
                        Log.d("SpotifyClient", "Token refreshed at ${System.currentTimeMillis()}")
                        // Update tokens
                        val updatedAccessToken = spotifyApi.token.accessToken
                        val updatedRefreshToken = spotifyApi.token.refreshToken
                        if (updatedRefreshToken != null) {
                            saveTokens(updatedAccessToken, updatedRefreshToken)
                        }
                    }
                }.build()
                Log.d("SpotifyClient", "Spotify API initialized with token: ${spotifyApi.token.accessToken}")
            } catch (e: Exception) {
                Log.d("SpotifyClient", "Failed to initialize Spotify API: ${e.message}")
            }
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        // Implement token saving logic here
        Log.d("SpotifyClient", "Tokens saved: Access token - $accessToken, Refresh token - $refreshToken")
        // Example: Save tokens to SharedPreferences or other storage
    }

    fun forceTokenRefresh() {
        runBlocking {
            try {
                Log.d("SpotifyClient", "Forcing token refresh...")
                val newToken = spotifyApi.refreshToken()
                Log.d("SpotifyClient", "New token: ${newToken.accessToken}")
                newToken.refreshToken?.let { saveTokens(newToken.accessToken, it) }
            } catch (e: Exception) {
                Log.d("SpotifyClient", "Failed to refresh token: ${e.message}")
            }
        }
    }
}
