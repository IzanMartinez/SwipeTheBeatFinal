package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InitializationViewModel : ViewModel() {
    // Propiedad mutable que indica si está inicializado
    private val _isInitialized = MutableLiveData(false)

    // Propiedad pública para observar el estado de inicialización
    val isInitialized: LiveData<Boolean> = _isInitialized

    // Función para establecer el estado de inicialización
    fun setInitialized() {
        _isInitialized.value = true
    }
}
