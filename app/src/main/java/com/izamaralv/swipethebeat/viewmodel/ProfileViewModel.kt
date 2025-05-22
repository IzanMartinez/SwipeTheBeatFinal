package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.repository.UserRepository
import com.izamaralv.swipethebeat.utils.changeColor

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    // ✅ Replace LiveData with simple variables
    private var userId: String = ""
    private var displayName: String = ""
    private var profileImageUrl: String = ""
    private var profileColor: String = ""

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

                this.profileColor = userData["profile_color"] ?: "#4444"

                // ✅ Immediately update the global color variable
                softComponentColor.value = Color(profileColor.toColorInt())
                changeColor(Color(profileColor.toColorInt()), userId, this)
                Log.d("ProfileViewModel", "✅ Profile color applied from Firestore: $profileColor")
            } else {
                Log.e("ProfileViewModel", "User not found in Firestore!")
            }
        }
    }




    fun changeColorInFirebase(userId: String, newColor: String) {
        Log.d("ProfileViewModel", "🔄 Updating profile color in Firestore: $newColor")

        profileColor = newColor
        softComponentColor.value = Color(newColor.toColorInt())

        userRepository.updateUserColor(userId, newColor) // ✅ Save in Firestore
    }
}

