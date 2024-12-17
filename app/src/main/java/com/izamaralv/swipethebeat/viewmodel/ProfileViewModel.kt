package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    // Propiedad mutable que contiene el nombre del usuario
    private val _displayName = MutableLiveData<String>()
    // Propiedad pública para observar el nombre del usuario
    val displayName: LiveData<String> get() = _displayName

    // Propiedad mutable que contiene la URL de la imagen del perfil del usuario
    private val _profileImageUrl = MutableLiveData<String>()
    // Propiedad pública para observar la URL de la imagen del perfil del usuario
    val profileImageUrl: LiveData<String> get() = _profileImageUrl

    // Función para establecer el nombre del usuario
    fun setDisplayName(name: String) {
        _displayName.postValue(name)
    }

    // Función para establecer la URL de la imagen del perfil del usuario
    fun setProfileImageUrl(url: String) {
        _profileImageUrl.postValue(url)
    }
}
