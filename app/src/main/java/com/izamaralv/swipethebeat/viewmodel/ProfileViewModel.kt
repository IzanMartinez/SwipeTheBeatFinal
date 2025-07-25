package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.models.Album
import com.izamaralv.swipethebeat.models.Artist
import com.izamaralv.swipethebeat.models.Image
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.repository.UserRepository
import com.izamaralv.swipethebeat.utils.changeColor

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private var userId: String = ""
    private var displayName: String = ""
    private var profileImageUrl: String = ""
    private var profileColor: String = ""

    // Estado para los artistas favoritos
    var favoriteArtist1 by mutableStateOf("")
        private set
    var favoriteArtist2 by mutableStateOf("")
        private set
    var favoriteArtist3 by mutableStateOf("")
        private set

    fun setUserId(uid: String) {
        userId = uid
    }

    fun getUserId(): String = userId
    fun getDisplayName(): String = displayName
    fun getProfileImageUrl(): String = profileImageUrl
    fun getProfileColor(): String = profileColor

    val savedSongs: SnapshotStateList<Track> = mutableStateListOf()

    fun saveUser(
        userData: Map<String, String>,
        onComplete: (() -> Unit)? = null
    ) {
        Log.d("ProfileViewModel", "Saving user to Firestore: $userData")
        userRepository.saveUserToFirestore(userData) {
            onComplete?.invoke()
        }
    }
    fun loadUserProfile(userId: String) {
        Log.d("ProfileViewModel", "➜ loadUserProfile() recibido userId = '$userId'")
        this.userId = userId

        userRepository.getUserFromFirestore(userId) { userData ->
            if (userData != null) {
                Log.d("ProfileViewModel", "⬇ userData de Firestore: $userData")

                displayName = userData["name"] ?: "Invitado"
                profileImageUrl = userData["avatar_url"] ?: ""
                profileColor = userData["profile_color"] ?: "#4444"
                Log.d("ProfileViewModel", "Color cargado: $profileColor")
                Log.d("ProfileViewModel", "▶ Campos asignados: displayName='$displayName', profileImageUrl='$profileImageUrl', profileColor='$profileColor'")

                // ▶ Immediately apply color
                softComponentColor.value = Color(profileColor.toColorInt())
                Log.d("ProfileViewModel", "✔ softComponentColor seteado a $profileColor")

                changeColor(Color(profileColor.toColorInt()), userId, this)
                Log.d("ProfileViewModel", "✅ Profile color applied: $profileColor")

                favoriteArtist1 = userData["favorite_artist1"] ?: ""
                favoriteArtist2 = userData["favorite_artist2"] ?: ""
                favoriteArtist3 = userData["favorite_artist3"] ?: ""
                Log.d("ProfileViewModel", "▶ Artistas cargados: '$favoriteArtist1', '$favoriteArtist2', '$favoriteArtist3'")

            } else {
                Log.e("ProfileViewModel", "❌ ¡No se encontró usuario en Firestore para $userId!")
            }
        }
    }


    /**
     * Actualiza el campo favorite_artistX en Firestore.
     */
    fun changeFavoriteArtist(slot: Int, artist: String) {
        if (userId.isBlank() || slot !in 0..2) return

        when (slot) {
            0 -> favoriteArtist1 = artist
            1 -> favoriteArtist2 = artist
            2 -> favoriteArtist3 = artist
        }

        val fieldName = "favorite_artist${slot + 1}"
        Log.d("ProfileViewModel", "🔄 Updating $fieldName in Firestore: $artist")

        userRepository.updateFavoriteArtist(userId, slot, artist)
    }

    fun changeColorInFirebase(userId: String, newColor: String) {
        Log.d("ProfileViewModel", "🔄 Updating profile color in Firestore: $newColor")
        profileColor = newColor
        softComponentColor.value = Color(newColor.toColorInt())
        userRepository.updateUserColor(userId, newColor)
    }

    fun loadSavedSongsToState() {
        if (userId.isBlank()) {
            Log.e("ProfileViewModel", "❌ loadSavedSongsToState: userId está vacío.")
            return
        }

        userRepository.getSavedSongs(userId) { listMaps ->
            Log.d("ProfileViewModel", "getSavedSongs devolvió ${listMaps.size} elementos.")

            // Convertimos cada Map<String, String> a un objeto Track
            val tracks = listMaps.mapNotNull { data ->
                val trackId   = data["id"] ?: return@mapNotNull null
                val trackName = data["name"] ?: return@mapNotNull null

                // Artistas guardados como “Artist1,Artist2,…”
                val artistsNames = data["artists"]?.split(",") ?: emptyList()
                val artists = artistsNames.map { artistName ->
                    Artist(
                        id     = "",            // No se guarda en Firestore
                        name   = artistName,
                        images = emptyList()
                    )
                }

                // Álbum: guardamos nombre + una única URL de imagen
                val albumName = data["album"] ?: ""
                val imageUrl  = data["imageUrl"] ?: ""
                val album = Album(
                    id     = "",                             // No se guarda en Firestore
                    name   = albumName,
                    images = listOf(Image(url = imageUrl))
                )

                Track(
                    name        = trackName,
                    artists     = artists,
                    album       = album,
                    previewUrl = null,                     // No guardamos preview_url
                    id          = trackId,
                    uri         = ""                        // No guardamos URI
                )
            }

            // Reemplazamos el contenido de la lista observable
            savedSongs.clear()
            savedSongs.addAll(tracks)
            Log.d("ProfileViewModel", "✅ savedSongs actualizadas: ${tracks.map { it.name }}")
        }
    }

    /**
     * Guarda la canción en Firestore bajo saved_songs.
     * Construye un Map con id, name, artists, album, imageUrl.
     */
    fun saveSongForLater(song: Track) {
        Log.d(
            "ProfileViewModel",
            "saveSongForLater() llamado con song='${song.name}', trackId='${song.id}', userId='$userId'"
        )

        if (userId.isBlank()){
            Log.e("ProfileViewModel", "❌ saveSongForLater: userId está vacío, no se puede guardar.")
            return
        }
        val data = mapOf(
            "id"       to song.id,
            "name"     to song.name,
            "artists"  to song.artists.joinToString(",") { it.name },
            "album"    to song.album.name,
            "imageUrl" to song.album.images.firstOrNull()?.url.orEmpty()
        )
        Log.d("ProfileViewModel", "Datos para Firestore = $data")

        userRepository.addSavedSong(userId, data)
    }

    /**
     *  Elimina la canción con ID = songId de Firestore y luego la elimina del
     *  SnapshotStateList savedSongs, disparando recomposición automática en UI.
     */
    /**
     * Borra de Firestore y de la lista local la canción en la posición `index`.
     */
    fun deleteSavedSongAt(index: Int) {
        if (userId.isBlank()) return
        if (index !in savedSongs.indices) return

        // Obtener ID antes de eliminar (para Firestore)
        val trackId = savedSongs[index].id

        // Borrar de Firestore
        userRepository.deleteSavedSong(userId, trackId)

        // Eliminar la posición exacta del SnapshotStateList
        savedSongs.removeAt(index)
        Log.d("ProfileViewModel", "✅ Track $trackId eliminado de savedSongs en posición $index")
    }

    /**
     * Recupera las canciones guardadas y las devuelve por callback.
     */
    fun loadSavedSongs(onResult: (List<Map<String, String>>) -> Unit) {
        if (userId.isBlank()) {
            onResult(emptyList())
            return
        }
        userRepository.getSavedSongs(userId, onResult)
    }


}
