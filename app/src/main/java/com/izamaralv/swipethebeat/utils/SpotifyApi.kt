package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.util.Log
import com.izamaralv.swipethebeat.utils.Credentials.REDIRECT_URI
import com.izamaralv.swipethebeat.utils.Credentials.SPOTIFY_CLIENT_ID
import com.izamaralv.swipethebeat.utils.Credentials.SPOTIFY_CLIENT_SECRET
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

object SpotifyApi {
    private val client = OkHttpClient()

    fun exchangeCodeForToken(code: String): Pair<String, String>? {
        val formBody = FormBody.Builder().add("grant_type", "authorization_code").add("code", code)
            .add("redirect_uri", REDIRECT_URI).add("client_id", SPOTIFY_CLIENT_ID)
            .add("client_secret", SPOTIFY_CLIENT_SECRET).build()

        val request =
            Request.Builder().url("https://accounts.spotify.com/api/token").post(formBody).build()

        // Registro de la solicitud de intercambio de código por token
        Log.d("SpotifyApi", "Exchange code for token request: $formBody")

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            // Registro del cuerpo de la respuesta
            Log.d("SpotifyApi", "Exchange code for token response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e(
                    "SpotifyApi",
                    "Exchange code for token failed: ${response.code} ${response.message}"
                )
                throw IOException("Unexpected code $response")
            }
            val jsonObject = responseBody?.let { JSONObject(it) }
            if (jsonObject != null) {
                val accessToken = jsonObject.getString("access_token")
                val refreshToken = jsonObject.getString("refresh_token")
                // Registro de los tokens obtenidos
                Log.d("SpotifyApi", "Access token: $accessToken, Refresh token: $refreshToken")
                return Pair(accessToken, refreshToken)
            }
        }
        return null
    }

    private fun refreshAccessToken(context: Context, refreshToken: String): String? {
        val formBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .add("client_id", SPOTIFY_CLIENT_ID)
            .add("client_secret", SPOTIFY_CLIENT_SECRET)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "Refresh response body: $responseBody")

            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Refresh token failed: ${response.code} ${response.message}")
                throw IOException("Unexpected code $response")
            }

            val jsonObject = responseBody?.let { JSONObject(it) }
            val newAccessToken = jsonObject?.getString("access_token")

            if (newAccessToken != null) {
                Log.d("SpotifyApi", "New access token: $newAccessToken")

                // ✅ Store the new access token globally in TokenManager
                TokenManager(context).saveAccessToken(newAccessToken)
                return newAccessToken
            }
        }
        return null
    }

    fun getUserProfile(accessToken: String, context: Context): Map<String, String?>? {
        Log.d("SpotifyApi", "Fetching user profile with access token: $accessToken")

        val request = Request.Builder().url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken").build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("SpotifyApi", "User profile response body: $responseBody")
            if (response.code == 401) { // 🔥 El token ha expirado -> refresca
                val newAccessToken = refreshAccessToken(context, TokenManager(context).getRefreshToken() ?: "")
                Log.d("SpotifyApi", "Refreshed access token: $newAccessToken")
                if (newAccessToken != null) {
                    return getUserProfile(newAccessToken, context) // 🔄 Retry with new token
                }
            }


            val jsonObject = responseBody?.let { JSONObject(it) }
            if (jsonObject == null) {
                Log.e("SpotifyApi", "❌ No JSON response received from Spotify API!")
                return null
            }

            val spotifyUserId = jsonObject.optString("id")
            if (spotifyUserId.isNullOrEmpty()) {
                Log.e("SpotifyApi", "❌ Extracted User ID is null or missing in API response!")
                return null
            }

            val displayName = jsonObject.optString("display_name")
            val email = jsonObject.optString("email")
            val avatarUrl = jsonObject.optJSONArray("images")?.optJSONObject(0)?.optString("url")

            Log.d("SpotifyApi", "✅ User Info Retrieved: ID=$spotifyUserId, Name=$displayName, Email=$email")

            return mapOf(
                "user_id" to spotifyUserId,
                "name" to displayName,
                "email" to email,
                "avatar_url" to avatarUrl
            )
        }
    }
}
