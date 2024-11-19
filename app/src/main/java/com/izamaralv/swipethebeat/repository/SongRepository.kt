package com.izamaralv.swipethebeat.repository

import android.content.Context
import com.izamaralv.swipethebeat.models.SongDTO
import com.izamaralv.swipethebeat.utils.SpotifyApi
import com.izamaralv.swipethebeat.utils.TokenManager
import android.util.Log

class SongRepository(private val context: Context) {

    private val tokenManager = TokenManager(context)

    suspend fun getUserTopTracks(): List<SongDTO>? {
        val accessToken = tokenManager.getAccessToken() ?: return null
        return try {
            SpotifyApi.getUserTopTracks(accessToken)
        } catch (e: Exception) {
            Log.e("SongRepository", "Failed to get user top tracks: ${e.message}")
            null
        }
    }

    suspend fun getUserLikedSongs(): List<SongDTO>? {
        val accessToken = tokenManager.getAccessToken() ?: return null
        return try {
            SpotifyApi.getUserLikedSongs(accessToken)
        } catch (e: Exception) {
            Log.e("SongRepository", "Failed to get user liked songs: ${e.message}")
            null
        }
    }

    suspend fun getRecommendations(): List<SongDTO>? {
        val accessToken = tokenManager.getAccessToken() ?: return null
        return try {
            SpotifyApi.getRecommendations(accessToken)
        } catch (e: Exception) {
            Log.e("SongRepository", "Failed to get recommendations: ${e.message}")
            null
        }
    }
}
