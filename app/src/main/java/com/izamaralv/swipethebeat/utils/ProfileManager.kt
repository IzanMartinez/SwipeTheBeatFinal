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
        Log.d("ProfileManager", "Fetching user profile with accessToken = $accessToken")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userProfile = SpotifyApi.getUserProfile(accessToken)
                Log.d("ProfileManager", "User profile fetched: $userProfile")

                userProfile?.let { profile ->
                    val userId = profile["user_id"] ?: ""
                    val displayName = profile["name"] ?: "No Name"
                    val email = profile["email"] ?: "No Email"
                    val profileImageUrl = profile["avatar_url"] ?: ""

                    Log.d("ProfileManager", "User ID: $userId, Name: $displayName, Email: $email, Image URL: $profileImageUrl")

                    val nonNullProfile = profile.mapValues { it.value ?: "" } // Convierte valores nulos en cadenas vacías

                    // Store data in Firestore
                    profileViewModel.saveUser(nonNullProfile)

                    // Load profile into UI
                    profileViewModel.loadUserProfile(userId)
                }


            } catch (e: IOException) {
                Log.e("ProfileManager", "Failed to fetch user profile: ${e.message}")
            }
        }
    }

}

