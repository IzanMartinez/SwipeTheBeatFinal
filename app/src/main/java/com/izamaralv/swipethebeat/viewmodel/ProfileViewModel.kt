package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izamaralv.swipethebeat.repository.UserRepository

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _displayName = MutableLiveData<String>()
    val displayName: LiveData<String> get() = _displayName
    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> get() = _profileImageUrl

    fun saveUser(userData: Map<String, String>) {
        Log.d("ProfileViewModel", "Saving user to Firestore: $userData") // ✅ Log before storing
        userRepository.saveUserToFirestore(userData)
    }

    fun loadUserProfile(userId: String) {
        Log.d("ProfileViewModel", "Loading user profile for ID: $userId") // ✅ Log before retrieving
        userRepository.getUserFromFirestore(userId) { userData ->
            if (userData != null) {
                Log.d("ProfileViewModel", "User data retrieved: $userData") // ✅ Log retrieved data
                _displayName.postValue(userData["name"] ?: "No Name")
                _profileImageUrl.postValue(userData["avatar_url"] ?: "")
            } else {
                Log.e("ProfileViewModel", "User not found in Firestore!") // ✅ Log if no user exists
            }
        }
    }

}

