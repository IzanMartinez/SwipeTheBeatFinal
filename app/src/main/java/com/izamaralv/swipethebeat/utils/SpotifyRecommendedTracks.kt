package com.izamaralv.swipethebeat.utils

import android.util.Log
import com.adamratzman.spotify.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.izamaralv.swipethebeat.utils.SpotifyClient

object SpotifyRecommendations {

    suspend fun getRecommendedTracks(seedTracks: List<String>, seedArtists: List<String>, seedGenres: List<String>): List<Track>? {
        return withContext(Dispatchers.IO) {
            try {
                // Check if seed parameters are not empty
                if (seedTracks.isEmpty() && seedArtists.isEmpty() && seedGenres.isEmpty()) {
                    Log.e("SpotifyRecommendations", "Seed parameters are empty, cannot fetch recommendations")
                    return@withContext null
                }

                // Build the URL for the recommendations request
                val url = "https://api.spotify.com/v1/recommendations?seed_tracks=${seedTracks.joinToString(",")}&seed_artists=${seedArtists.joinToString(",")}&seed_genres=${seedGenres.joinToString(",")}"
                Log.d("SpotifyRecommendations", "Recommendations request URL: $url")

                // Make the API call
                SpotifyClient.spotifyApi.browse.getRecommendations(seedTracks, seedArtists, seedGenres).tracks
            } catch (e: Exception) {
                Log.e("SpotifyRecommendations", "Failed to get recommendations: ${e.message}")
                null
            }
        }
    }
}
