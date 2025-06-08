package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

// Datos básicos del perfil de usuario
data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("email") val email: String,
    @SerializedName("images") val images: List<ProfileImage>
)

// Imagen asociada al perfil de usuario
data class ProfileImage(
    @SerializedName("url") val url: String
)
