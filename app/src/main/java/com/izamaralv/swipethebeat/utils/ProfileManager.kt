package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.util.Log
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class ProfileManager(private val context: Context, private val profileViewModel: ProfileViewModel) {

    fun fetchUserProfile(accessToken: String) {
        // Registro para indicar que se está obteniendo el perfil de usuario
        Log.d("ProfileManager", "Fetching user profile with accessToken = $accessToken")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Obtiene el perfil de usuario desde Spotify API
                val userProfile = SpotifyApi.getUserProfile(accessToken)
                Log.d("ProfileManager", "User profile fetched: $userProfile")

                userProfile?.let { profile ->
                    Log.d("ProfileManager", "User profile JSON: ${profile.toString(2)}")
                    val displayName = profile.optString("display_name", "No Name")
                    val profileImageUrl = profile.getJSONArray("images").optJSONObject(0)?.getString("url") ?: ""

                    Log.d("ProfileManager", "User display name: $displayName")
                    Log.d("ProfileManager", "User profile image URL: $profileImageUrl")

                    // Actualiza el ViewModel con el nombre y la URL de la imagen del perfil
                    profileViewModel.setDisplayName(displayName)
                    profileViewModel.setProfileImageUrl(profileImageUrl)
                    Log.d("ProfileManager", "ProfileViewModel updated")
                }
            } catch (e: IOException) {
                Log.e("ProfileManager", "Failed to fetch user profile: ${e.message}")
                if (e.message?.contains("401 Unauthorized") == true) {
                    // Maneja el caso en el que el token ha expirado
                    Log.d("ProfileManager", "Token expired, refreshing token")
                    val tokenManager = TokenManager(context)
                    val refreshToken = tokenManager.getRefreshToken()

                    refreshToken?.let {
                        // Obtiene un nuevo token de acceso y actualiza el perfil de usuario
                        val newAccessToken = SpotifyApi.refreshAccessToken(it)
                        newAccessToken?.let { newToken ->
                            tokenManager.saveTokens(newToken, it)
                            SpotifyClient.initialize(newToken, it, 3600)
                            fetchUserProfile(newToken)
                        }
                    }
                }
            }
        }
    }
}
