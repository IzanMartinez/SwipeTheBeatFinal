package com.izamaralv.swipethebeat.network

import com.izamaralv.swipethebeat.models.LikedSongsResponse
import com.izamaralv.swipethebeat.models.SearchResponse
import com.izamaralv.swipethebeat.models.UserProfile
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

// Interfaz del servicio Spotify API
interface SpotifyApiService {

    @GET("v1/me")
    suspend fun getCurrentUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfile> // Obtener perfil de usuario

    @GET("v1/me/tracks")
    suspend fun getLikedSongs(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int
    ): Response<LikedSongsResponse> // Obtener canciones favoritas

    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 50
    ): Response<SearchResponse> // Buscar tracks

    @PUT("v1/me/tracks")
    suspend fun likeTrack(
        @Header("Authorization") token: String,
        @Query("ids") trackId: String
    ): Response<Unit> // Añadir track a favoritos

    @DELETE("v1/me/tracks")
    suspend fun removeTrackFromLibrary(
        @Header("Authorization") token: String,
        @Query("ids") trackId: String
    ): Response<Unit> // Eliminar track de favoritos

    @GET("v1/search")
    suspend fun searchArtists(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("limit") limit: Int = 15
    ): Response<SearchResponse> // Buscar artistas

    @GET("v1/search")
    suspend fun searchExactTrack(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 1
    ): Response<SearchResponse> // Buscar track exacta

}

