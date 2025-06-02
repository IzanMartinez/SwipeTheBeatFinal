package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class ProfileManager(
    private val context: Context,
    private val profileViewModel: ProfileViewModel
) {

    fun fetchUserProfile(accessToken: String) {
        Log.d("ProfileManager", "Fetching user profile with accessToken = $accessToken")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userProfile = SpotifyApi.getUserProfile(accessToken, context)
                Log.d("ProfileManager", "User profile fetched: $userProfile")

                userProfile?.let { profile ->
                    val userId = profile["user_id"] ?: ""
                    Log.d("ProfileManager", "▶ Extraído userId = '$userId'")

                    val displayName = profile["name"] ?: "No Name"
                    val email = profile["email"] ?: "No Email"
                    val profileImageUrl = profile["avatar_url"] ?: ""

                    val hexColor =
                        String.format("#%06X", (softComponentColor.value.toArgb() and 0xFFFFFF))

                    // Almacenamos temporalmente el userId en el ViewModel
                    profileViewModel.setUserId(userId)
                    Log.d("ProfileManager", "✔ Llamado a profileViewModel.setUserId('$userId')")

                    // Construimos el mapa completo para Firestore
                    val nonNullProfile = profile.mapValues { it.value ?: "" }.toMutableMap()
                    nonNullProfile["profile_color"] = hexColor

                    // 1) Guardamos en Firestore. Sólo cuando termine, cargamos el perfil.
                    profileViewModel.saveUser(nonNullProfile) {
                        Log.d("ProfileManager", "✔ Guardado básico en Firestore: $nonNullProfile")
                        // 2) Ahora que Firestore ya tiene el documento, lo leemos
                        profileViewModel.loadUserProfile(userId)
                        Log.d("ProfileManager", "✔ Llamado a profileViewModel.loadUserProfile('$userId')")
                    }
                }

            } catch (e: IOException) {
                Log.e("ProfileManager", "❌ Error en fetchUserProfile: ${e.message}")
            }
        }
    }
}
