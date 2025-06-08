package com.izamaralv.swipethebeat.repository

import android.util.Log
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.models.UserProfile
import com.izamaralv.swipethebeat.network.SpotifyApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Gestiona las llamadas a la API de Spotify para perfil de usuario y tracks.
 */
class SongRepository {

    private val apiService: SpotifyApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(SpotifyApiService::class.java)
    }

    /** Obtiene los datos básicos del perfil actual */
    suspend fun getCurrentUserProfile(token: String): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentUserProfile(token = "Bearer $token")
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("SongRepository", "Error al cargar perfil: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SongRepository", "Excepción en getCurrentUserProfile: ${e.message}")
            null
        }
    }

    /** Recupera las últimas 50 canciones marcadas como “me gusta” */
    suspend fun getLast50LikedSongs(token: String): List<Track> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLikedSongs(token = "Bearer $token", limit = 50)
            if (response.isSuccessful) {
                response.body()?.tracks?.map { it.track } ?: emptyList()
            } else {
                Log.e("SongRepository", "Error al cargar liked songs: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SongRepository", "Excepción en getLast50LikedSongs: ${e.message}")
            emptyList()
        }
    }

    /** Búsqueda aproximada de pistas según un texto libre */
    suspend fun searchSimilarTracks(token: String, query: String): List<Track> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchTracks(token = "Bearer $token", query = query)
            if (response.isSuccessful) {
                response.body()?.tracks?.items?.map {
                    Track(
                        name = it.name,
                        artists = it.artists,
                        album = it.album,
                        previewUrl = it.previewUrl,
                        id = it.id,
                        uri = it.uri
                    )
                } ?: emptyList()
            } else {
                Log.e("SongRepository", "Error en searchSimilarTracks: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SongRepository", "Excepción en searchSimilarTracks: ${e.message}")
            emptyList()
        }
    }

    /** Busca la primera coincidencia exacta de un título */
    suspend fun searchExactTrack(token: String, query: String): Track? = withContext(Dispatchers.IO) {
        try {
            val response: Response<com.izamaralv.swipethebeat.models.SearchResponse> =
                apiService.searchExactTrack("Bearer $token", query)
            if (response.isSuccessful) {
                response.body()?.tracks?.items?.firstOrNull()?.let {
                    Track(
                        name = it.name,
                        artists = it.artists,
                        album = it.album,
                        previewUrl = it.previewUrl,
                        id = it.id,
                        uri = it.uri
                    )
                }
            } else {
                Log.e("SongRepository", "Error en searchExactTrack: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SongRepository", "Excepción en searchExactTrack: ${e.message}")
            null
        }
    }

    /** Marca una pista como “me gusta” en Spotify */
    suspend fun likeTrack(accessToken: String, trackId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.likeTrack(token = "Bearer $accessToken", trackId = trackId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SongRepository", "Excepción en likeTrack: ${e.message}")
            false
        }
    }

    /** Elimina una pista de “me gusta” en Spotify */
    suspend fun dislikeTrack(token: String, trackId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.removeTrackFromLibrary(token = "Bearer $token", trackId = trackId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SongRepository", "Excepción en dislikeTrack: ${e.message}")
            false
        }
    }

    /** Busca nombres de artistas coincidentes */
    suspend fun searchArtists(token: String, query: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchArtists(token = "Bearer $token", query = query)
            if (response.isSuccessful) {
                response.body()?.artists?.items?.map { it.name } ?: emptyList()
            } else {
                Log.e("SongRepository", "Error en searchArtists: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SongRepository", "Excepción en searchArtists: ${e.message}")
            emptyList()
        }
    }
}
