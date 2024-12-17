package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.izamaralv.swipethebeat.repository.SongRepository

class SongViewModelFactory(
    private val songRepository: SongRepository,
    private val accessToken: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica si la clase del modelo es SongViewModel
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            // Crea una instancia de SongViewModel con los parámetros proporcionados
            return SongViewModel(songRepository, accessToken) as T
        }
        // Lanza una excepción si la clase del modelo es desconocida
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
