package com.izamaralv.swipethebeat.repository

import android.content.Context
import android.util.Log
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.models.UserProfile
import com.izamaralv.swipethebeat.network.SpotifyApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongRepository(context: Context) {

    private val apiService: SpotifyApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/") // Base URL de la API de Spotify
            .addConverterFactory(GsonConverterFactory.create()) // Convertidor de Gson
            .build()
        apiService = retrofit.create(SpotifyApiService::class.java) // Creación del servicio API
    }

    suspend fun getCurrentUserProfile(token: String): UserProfile? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentUserProfile(token = "Bearer $token")
                if (response.isSuccessful) {
                    response.body() // Devuelve el perfil de usuario si la respuesta es exitosa
                } else {
                    Log.e("SongRepository", "Failed to get user profile: ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("SongRepository", "Exception in getCurrentUserProfile: ${e.message}")
                null
            }
        }
    }

    suspend fun getLast50LikedSongs(token: String): List<Track> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLikedSongs(
                    token = "Bearer $token",
                    limit = 25
                )
                if (response.isSuccessful) {
                    Log.d(
                        "SongRepository",
                        "Liked songs response: ${response.body()?.tracks?.map { it.track.name }}"
                    )
                    response.body()?.tracks?.map { it.track } ?: emptyList() // Devuelve las canciones favoritas
                } else {
                    Log.e(
                        "SongRepository",
                        "Failed to get last 25 liked songs: ${response.message()} (Code: ${response.code()})"
                    )
                    Log.e("SongRepository", "Response body: ${response.errorBody()?.string()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("SongRepository", "Exception in getLast25LikedSongs: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun searchSimilarTracks(token: String, query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchTracks(
                    token = "Bearer $token",
                    query = query
                )
                if (response.isSuccessful) {
                    Log.d(
                        "SongRepository",
                        "Search tracks response: ${response.body()?.tracks?.items?.map { it.name }}"
                    )
                    response.body()?.tracks?.items?.map {
                        Track(
                            name = it.name,
                            artists = it.artists,
                            album = it.album,
                            preview_url = it.preview_url,
                            id = it.id,
                            uri = it.uri
                        )
                    } ?: emptyList() // Devuelve la lista de tracks encontrados
                } else {
                    Log.e(
                        "SongRepository",
                        "Failed to search similar tracks: ${response.message()} (Code: ${response.code()})"
                    )
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("SongRepository", "Exception in searchSimilarTracks: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun likeTrack(accessToken: String, trackId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<Unit> = apiService.likeTrack(
                    token = "Bearer $accessToken",
                    trackId = trackId
                )
                if (response.isSuccessful) {
                    true // Retorna true si la respuesta es exitosa
                } else {
                    Log.e("SongRepository", "Failed to like track: ${response.message()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("SongRepository", "Exception in likeTrack: ${e.message}")
                false
            }
        }
    }

    suspend fun dislikeTrack(token: String, trackId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<Unit> = apiService.removeTrackFromLibrary(token = "Bearer $token", trackId = trackId)
                if (response.isSuccessful) {
                    true // Retorna true si la respuesta es exitosa
                } else {
                    Log.e("SongRepository", "Failed to dislike track: ${response.message()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("SongRepository", "Exception in dislikeTrack: ${e.message}")
                false
            }
        }
    }
    suspend fun searchArtists(token: String, query: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchArtists(
                    token = "Bearer $token",
                    query = query
                )

                if (response.isSuccessful) {
                    Log.d("SongRepository", "Search artists response: ${response.body()?.artists?.items?.map { it.name }}")
                    response.body()?.artists?.items?.map { it.name } ?: emptyList() // ✅ Returns artist names only
                } else {
                    Log.e("SongRepository", "Failed to search artists: ${response.message()} (Code: ${response.code()})")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("SongRepository", "Exception in searchArtists: ${e.message}")
                emptyList()
            }
        }
    }

}

