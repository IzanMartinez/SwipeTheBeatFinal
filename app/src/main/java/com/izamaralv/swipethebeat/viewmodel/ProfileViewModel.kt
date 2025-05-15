package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.repository.UserRepository
import androidx.core.graphics.toColorInt

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId
    private val _displayName = MutableLiveData<String>()
    val displayName: LiveData<String> get() = _displayName
    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> get() = _profileImageUrl
    private val _profileColor = MutableLiveData<String>()
    val profileColor: LiveData<String> get() = _profileColor

    fun saveUser(userData: Map<String, String>) {
        Log.d("ProfileViewModel", "Saving user to Firestore: $userData") // ✅ Log before storing
        userRepository.saveUserToFirestore(userData)
    }

    fun loadUserProfile(userId: String) {
        Log.d("ProfileViewModel", "Loading user profile for ID: $userId") // ✅ Log before retrieving
        userRepository.getUserFromFirestore(userId) { userData ->
            if (userData != null) {
                Log.d("ProfileViewModel", "User data retrieved: $userData") // ✅ Log retrieved data
                _userId.postValue(userData["user_id"] ?: "")
                _displayName.postValue(userData["name"] ?: "No Name")
                _profileImageUrl.postValue(userData["avatar_url"] ?: "")
                val savedColor = userData["profile_color"] ?: "#3be477"
                _profileColor.postValue(savedColor)
                softComponentColor.value = Color(savedColor.toColorInt())
            } else {
                Log.e("ProfileViewModel", "User not found in Firestore!") // ✅ Log if no user exists
            }
        }
    }

    fun getUserId(): String? = _userId.value

    fun changeColor(userId: String, newColor: String) {
        Log.d("ProfileViewModel", "🔄 Updating profile color in Firestore: $newColor")

        _profileColor.postValue(newColor)
        softComponentColor.value = Color(android.graphics.Color.parseColor(newColor))

        userRepository.updateUserColor(userId, newColor) // ✅ Save in Firestore
    }




}

