package com.izamaralv.swipethebeat.utils

import android.util.Log
import com.izamaralv.swipethebeat.models.SongDTO
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object SpotifyApi {
    private val client = OkHttpClient()

    fun exchangeCodeForToken(code: String): Pair<String, String>? {
        val clientId = "9ce30545b1c64f29844917fae59145c7"
        val clientSecret = "a62d19455f554f2c8c8795e89dabde13"
        val redirectUri = "myapp://callback"

        val formBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", redirectUri)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(formBody)
            .build()

        Log.d("SpotifyApi", "Exchange code for token request: ${formBody.toString()}")

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "Exchange code for token response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Exchange code for token failed: ${response.code} ${response.message}")
                throw IOException("Unexpected code $response")
            }
            val jsonObject = responseBody?.let { JSONObject(it) }
            if (jsonObject != null) {
                val accessToken = jsonObject.getString("access_token")
                val refreshToken = jsonObject.getString("refresh_token")
                Log.d("SpotifyApi", "Access token: $accessToken, Refresh token: $refreshToken")
                return Pair(accessToken, refreshToken)
            }
        }
        return null
    }

    fun refreshAccessToken(refreshToken: String): String? {
        val clientId = "9ce30545b1c64f29844917fae59145c7"
        val clientSecret = "a62d19455f554f2c8c8795e89dabde13"

        val formBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(formBody)
            .build()

        Log.d("SpotifyApi", "Refresh token request: ${formBody.toString()}")

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "Refresh response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Refresh token failed: ${response.code} ${response.message}")
                throw IOException("Unexpected code $response")
            }
            val jsonObject = responseBody?.let { JSONObject(it) }
            if (jsonObject != null) {
                val accessToken = jsonObject.getString("access_token")
                Log.d("SpotifyApi", "New access token: $accessToken")
                return accessToken
            }
        }
        return null
    }

    fun getUserProfile(accessToken: String): JSONObject? {
        Log.d("SpotifyApi", "Fetching user profile with access token: $accessToken")
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "User profile response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Failed to fetch user profile: ${response.code} ${response.message}")
                if (response.code == 401) {
                    throw IOException("401 Unauthorized - Token may be expired")
                } else {
                    throw IOException("Unexpected code $response")
                }
            }
            return responseBody?.let { JSONObject(it) }
        }
    }

    fun getUserTopTracks(accessToken: String): List<SongDTO>? {
        Log.d("SpotifyApi", "Fetching user top tracks with access token: $accessToken")
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/top/tracks")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "User top tracks response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Failed to fetch user top tracks: ${response.code} ${response.message}")
                throw IOException("Unexpected code $response")
            }
            val jsonObject = responseBody?.let { JSONObject(it) }
            val items = jsonObject?.getJSONArray("items")
            return items?.let { parseSongs(it) }
        }
    }

    fun getUserLikedSongs(accessToken: String): List<SongDTO>? {
        Log.d("SpotifyApi", "Fetching user liked songs with access token: $accessToken")
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/tracks")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "User liked songs response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Failed to fetch user liked songs: ${response.code} ${response.message}")
                throw IOException("Unexpected code $response")
            }
            val jsonObject = responseBody?.let { JSONObject(it) }
            val items = jsonObject?.getJSONArray("items")
            return items?.let { parseSongs(it) }
        }
    }

    fun getRecommendations(accessToken: String): List<SongDTO>? {
        val seedTracks = getSeedTracks(accessToken)
        Log.d("SpotifyApi", "Fetching recommendations with seed tracks: $seedTracks")
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/recommendations?seed_tracks=$seedTracks")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "Recommendations response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Failed to fetch recommendations: ${response.code} ${response.message}")
                throw IOException("Unexpected code $response")
            }
            val jsonObject = responseBody?.let { JSONObject(it) }
            val items = jsonObject?.getJSONArray("tracks")
            return items?.let { parseSongs(it) }
        }
    }

    private fun getSeedTracks(accessToken: String): String {
        Log.d("SpotifyApi", "Getting seed tracks with access token: $accessToken")
        val likedSongs = getUserLikedSongs(accessToken) ?: emptyList()
        val topTracks = getUserTopTracks(accessToken) ?: emptyList()
        Log.d("SpotifyApi", "Liked songs: ${likedSongs.size}, Top tracks: ${topTracks.size}")
        val seedTracks = likedSongs + topTracks
        val seedTrackIds = seedTracks.joinToString(",") { it.id }
        Log.d("SpotifyApi", "Seed track IDs: $seedTrackIds")
        return seedTrackIds
    }

    private fun parseSongs(items: JSONArray): List<SongDTO> {
        Log.d("SpotifyApi", "Parsing songs from items: ${items.length()}")
        val songs = mutableListOf<SongDTO>()
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
            val id = item.getString("id")
            val name = item.getString("name")
            val artists = item.getJSONArray("artists").let { artistsArray ->
                List(artistsArray.length()) { index ->
                    artistsArray.getJSONObject(index).getString("name")
                }
            }
            val album = item.getJSONObject("album")
            val albumName = album.getString("name")
            val albumCoverUrl = album.getJSONArray("images").getJSONObject(0).getString("url")
            val durationMs = item.getInt("duration_ms")
            val uri = item.getString("uri")
            songs.add(SongDTO(id, name, artists, albumName, albumCoverUrl, durationMs, uri))
        }
        Log.d("SpotifyApi", "Parsed ${songs.size} songs")
        return songs
    }
}
