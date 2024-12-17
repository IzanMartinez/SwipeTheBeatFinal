package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

// Datos del perfil del usuario
data class UserProfile(
    @SerializedName("id") val id: String, // ID del usuario
    @SerializedName("display_name") val displayName: String, // Nombre de usuario mostrado
    @SerializedName("email") val email: String, // Email del usuario
    @SerializedName("images") val images: List<ProfileImage> // Lista de imágenes del perfil
)

// Datos de la imagen del perfil
data class ProfileImage(
    @SerializedName("url") val url: String // URL de la imagen del perfil
)
