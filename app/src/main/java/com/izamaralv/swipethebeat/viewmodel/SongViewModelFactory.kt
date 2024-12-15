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
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            return SongViewModel(songRepository, accessToken) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
