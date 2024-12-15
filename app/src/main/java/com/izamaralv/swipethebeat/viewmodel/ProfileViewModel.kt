package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _displayName = MutableLiveData<String>()
    val displayName: LiveData<String> get() = _displayName

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> get() = _profileImageUrl

    fun setDisplayName(name: String) {
        _displayName.postValue(name)
    }

    fun setProfileImageUrl(url: String) {
        _profileImageUrl.postValue(url)
    }
}
