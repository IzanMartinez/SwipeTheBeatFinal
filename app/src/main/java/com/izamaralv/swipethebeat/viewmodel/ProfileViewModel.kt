package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.repository.UserRepository
import androidx.core.graphics.toColorInt

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    // ✅ Replace LiveData with simple variables
    private var userId: String = ""
    private var displayName: String = "Invitado"
    private var profileImageUrl: String = ""
    private var profileColor: String = "#3be477"

    fun getUserId(): String = userId
    fun getDisplayName(): String = displayName
    fun getProfileImageUrl(): String = profileImageUrl
    fun getProfileColor(): String = profileColor

    fun saveUser(userData: Map<String, String>) {
        Log.d("ProfileViewModel", "Saving user to Firestore: $userData")
        userRepository.saveUserToFirestore(userData)
    }

    fun loadUserProfile(userId: String) {
        Log.d("ProfileViewModel", "Loading user profile for ID: $userId")

        userRepository.getUserFromFirestore(userId) { userData ->
            if (userData != null) {
                Log.d("ProfileViewModel", "User data retrieved: $userData")

                this.userId = userData["user_id"] ?: ""
                this.displayName = userData["name"] ?: "Invitado"
                this.profileImageUrl = userData["avatar_url"] ?: ""

                val storedColorHex = userData["profile_color"] ?: "#3be477" // ✅ Firestore stored color

                // ✅ Immediately update the global color variable
                softComponentColor.value = Color(storedColorHex.toColorInt())

                Log.d("ProfileViewModel", "✅ Profile color applied from Firestore: $storedColorHex")
            } else {
                Log.e("ProfileViewModel", "User not found in Firestore!")
            }
        }
    }




    fun changeColor(userId: String, newColor: String) {
        Log.d("ProfileViewModel", "🔄 Updating profile color in Firestore: $newColor")

        profileColor = newColor
        softComponentColor.value = Color(newColor.toColorInt())

        userRepository.updateUserColor(userId, newColor) // ✅ Save in Firestore
    }
}

