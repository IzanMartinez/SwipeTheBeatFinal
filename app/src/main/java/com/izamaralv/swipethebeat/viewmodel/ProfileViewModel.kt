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

    // ▶ State for the 3 favorite‐artist slots
    private var favoriteArtist1: String = ""
    private var favoriteArtist2: String = ""
    private var favoriteArtist3: String = ""

    fun getUserId(): String = userId
    fun getDisplayName(): String = displayName
    fun getProfileImageUrl(): String = profileImageUrl
    fun getProfileColor(): String = profileColor
    fun getFavoriteArtist1(): String = favoriteArtist1
    fun getFavoriteArtist2(): String = favoriteArtist2
    fun getFavoriteArtist3(): String = favoriteArtist3

    fun saveUser(userData: Map<String, String>) {
        Log.d("ProfileViewModel", "Saving user to Firestore: $userData")
        userRepository.saveUserToFirestore(userData)

        val uid = userData["user_id"] ?: return
        userRepository.initializeUserProfile(uid, userData)
        userRepository.saveUserToFirestore(userData)
    }

    fun loadUserProfile(userId: String) {
        Log.d("ProfileViewModel", "Loading user profile for ID: $userId")
        this.userId = userId

        userRepository.getUserFromFirestore(userId) { userData ->
            if (userData != null) {
                Log.d("ProfileViewModel", "User data retrieved: $userData")

                // ▶ Core profile fields
                displayName = userData["name"] ?: "Invitado"
                profileImageUrl = userData["avatar_url"] ?: ""
                profileColor = userData["profile_color"] ?: "#4444"

                // ▶ Immediately apply color
                softComponentColor.value = Color(profileColor.toColorInt())
                changeColor(Color(profileColor.toColorInt()), userId, this)
                Log.d("ProfileViewModel", "✅ Profile color applied: $profileColor")

                // ▶ Load favorite artists
                favoriteArtist1 = userData["favorite_artist1"] ?: ""
                favoriteArtist2 = userData["favorite_artist2"] ?: ""
                favoriteArtist3 = userData["favorite_artist3"] ?: ""
                Log.d("ProfileViewModel", "✅ Favorite artists loaded: " +
                        "$favoriteArtist1, $favoriteArtist2, $favoriteArtist3")
            } else {
                Log.e("ProfileViewModel", "User not found in Firestore!")
            }
        }
    }

    /**
     * ▶ Single method to update any of the three favorite‐artist slots.
     * slot: 0→favorite_artist1, 1→favorite_artist2, 2→favorite_artist3
     */
    fun changeFavoriteArtist(slot: Int, artist: String) {
        if (userId.isBlank() || slot !in 0..2) return

        // ▶ Update local variable
        when (slot) {
            0 -> favoriteArtist1 = artist
            1 -> favoriteArtist2 = artist
            2 -> favoriteArtist3 = artist
        }

        // ▶ Log uses the correct field name
        val fieldName = "favorite_artist${slot + 1}"
        Log.d("ProfileViewModel", "🔄 Updating $fieldName in Firestore: $artist")

        // ▶ Delegate to repository
        userRepository.updateFavoriteArtist(userId, slot, artist)
    }

    fun changeColorInFirebase(userId: String, newColor: String) {
        Log.d("ProfileViewModel", "🔄 Updating profile color in Firestore: $newColor")
        profileColor = newColor
        softComponentColor.value = Color(newColor.toColorInt())
        userRepository.updateUserColor(userId, newColor)
    }
}
