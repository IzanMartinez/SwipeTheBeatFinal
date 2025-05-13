package com.izamaralv.swipethebeat.utils

import android.util.Log
import com.izamaralv.swipethebeat.models.SongDTO
import com.izamaralv.swipethebeat.utils.Credentials.CLIENT_ID
import com.izamaralv.swipethebeat.utils.Credentials.CLIENT_SECRET
import com.izamaralv.swipethebeat.utils.Credentials.REDIRECT_URI
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object SpotifyApi {
    private val client = OkHttpClient()

    fun exchangeCodeForToken(code: String): Pair<String, String>? {
        val formBody = FormBody.Builder().add("grant_type", "authorization_code").add("code", code)
            .add("redirect_uri", REDIRECT_URI).add("client_id", CLIENT_ID)
            .add("client_secret", CLIENT_SECRET).build()

        val request =
            Request.Builder().url("https://accounts.spotify.com/api/token").post(formBody).build()

        // Registro de la solicitud de intercambio de código por token
        Log.d("SpotifyApi", "Exchange code for token request: ${formBody.toString()}")

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

    fun refreshAccessToken(refreshToken: String): String? {
        val formBody =
            FormBody.Builder().add("grant_type", "refresh_token").add("refresh_token", refreshToken)
                .add("client_id", CLIENT_ID).add("client_secret", CLIENT_SECRET).build()

        val request =
            Request.Builder().url("https://accounts.spotify.com/api/token").post(formBody).build()

        // Registro de la solicitud de actualización de token
        Log.d("SpotifyApi", "Refresh token request: ${formBody.toString()}")

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            // Registro del cuerpo de la respuesta de actualización
            Log.d("SpotifyApi", "Refresh response body: $responseBody")
            if (!response.isSuccessful) {
                Log.e("SpotifyApi", "Refresh token failed: ${response.code} ${response.message}")
                throw IOException("Unexpected code $response")
            }
            val jsonObject = responseBody?.let { JSONObject(it) }
            if (jsonObject != null) {
                val accessToken = jsonObject.getString("access_token")
                // Registro del nuevo token de acceso
                Log.d("SpotifyApi", "New access token: $accessToken")
                return accessToken
            }
        }
        return null
    }

    fun getUserProfile(accessToken: String): Map<String, String?>? {
        // Registro de la obtención del perfil de usuario
        Log.d("SpotifyApi", "Fetching user profile with access token: $accessToken")
        val request = Request.Builder().url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken").build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            // Registro del cuerpo de la respuesta del perfil de usuario
            Log.d("SpotifyApi", "User profile response body: $responseBody")

            if (!response.isSuccessful) {
                Log.e(
                    "SpotifyApi",
                    "Failed to fetch user profile: ${response.code} ${response.message}"
                )
                if (response.code == 401) {
                    throw IOException("401 Unauthorized - Token may be expired")
                } else {
                    throw IOException("Unexpected code $response")
                }
            }

            val jsonObject = responseBody?.let { JSONObject(it) }
            return jsonObject?.let {
                val spotifyUserId = it.getString("id") // Registro del ID de Spotify del usuario
                val displayName = it.optString("display_name") // Registro del nombre de usuario
                val email = it.optString("email") // Registro del email del usuario
                val avatarUrl = it.optJSONArray("images")?.optJSONObject(0)
                    ?.optString("url") // Registro de la URL de la imagen de avatar")

                Log.d(
                    "SpotifyApi",
                    "User Info Retrieved: ID=$spotifyUserId, Name=$displayName, Email=$email"
                )

                mapOf(
                    "user_id" to spotifyUserId,
                    "name" to displayName,
                    "email" to email,
                    "avatar_url" to avatarUrl
                )
            }
        }


    }
}
