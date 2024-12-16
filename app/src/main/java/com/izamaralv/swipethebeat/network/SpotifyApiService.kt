package com.izamaralv.swipethebeat.network

import com.izamaralv.swipethebeat.models.LikedSongsResponse
import com.izamaralv.swipethebeat.models.SearchResponse
import com.izamaralv.swipethebeat.models.UserProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface SpotifyApiService {

    @GET("v1/me")
    suspend fun getCurrentUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfile>

    @GET("v1/me/tracks")
    suspend fun getLikedSongs(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int
    ): Response<LikedSongsResponse>

    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 25
    ): Response<SearchResponse>

    @PUT("v1/me/tracks")
    suspend fun likeTrack(
        @Header("Authorization") token: String,
        @Query("ids") trackId: String
    ): Response<Unit>
}
