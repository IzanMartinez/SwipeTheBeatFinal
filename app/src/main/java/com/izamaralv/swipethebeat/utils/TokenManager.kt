package com.izamaralv.swipethebeat.utils

import android.content.Context

class TokenManager(context: Context) {
    // Accede a las preferencias compartidas para almacenar los tokens de Spotify
    private val sharedPreferences = context.getSharedPreferences("SpotifyTokens", Context.MODE_PRIVATE)

    // Guarda los tokens de acceso y actualización en las preferencias compartidas
    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString("ACCESS_TOKEN", accessToken)
            .putString("REFRESH_TOKEN", refreshToken)
            .apply()
    }

    // Recupera el token de acceso almacenado
    fun getAccessToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    // Recupera el token de actualización almacenado
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("REFRESH_TOKEN", null)
    }

    // Limpia los tokens almacenados en las preferencias compartidas
    fun clearTokens() {
        sharedPreferences.edit()
            .remove("ACCESS_TOKEN")
            .remove("REFRESH_TOKEN")
            .apply()
    }
}

