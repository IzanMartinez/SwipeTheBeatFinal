package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileVIewModel : ViewModel() {
    val displayName = MutableLiveData<String>()
    val profileImageUrl = MutableLiveData<String>()
}