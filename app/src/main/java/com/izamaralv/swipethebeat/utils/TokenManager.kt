package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit

class TokenManager(context: Context) {
    // Accede a las preferencias compartidas para almacenar los tokens de Spotify
    private val sharedPreferences = context.getSharedPreferences("SpotifyTokens", Context.MODE_PRIVATE)

    // Guarda los tokens de acceso y actualización en las preferencias compartidas
    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit {
            putString("ACCESS_TOKEN", accessToken)
                .putString("REFRESH_TOKEN", refreshToken)
        }
    }

    // Recupera el token de acceso almacenado
    fun getAccessToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    // Recupera el token de actualización almacenado
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("REFRESH_TOKEN", null)
    }

    // Guarda un nuevo token de acceso
        fun saveAccessToken(newAccessToken: String) {
            sharedPreferences.edit { putString("ACCESS_TOKEN", newAccessToken) }
            Log.d("TokenManager", "✅ Saved new access token: $newAccessToken")
        }
}

