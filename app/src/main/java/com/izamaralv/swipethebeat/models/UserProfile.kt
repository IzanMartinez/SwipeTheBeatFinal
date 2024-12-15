package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("email") val email: String,
    @SerializedName("images") val images: List<ProfileImage>
)

data class ProfileImage(
    @SerializedName("url") val url: String
)
