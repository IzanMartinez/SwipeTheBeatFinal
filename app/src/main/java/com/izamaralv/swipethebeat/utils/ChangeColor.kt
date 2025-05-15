package com.izamaralv.swipethebeat.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

fun changeColor(color: Color, userId: String, profileViewModel: ProfileViewModel) {
    Log.d("ProfileViewModel", "🔄 Updating profile color to: $color")

    // ✅ Apply color change globally
    softComponentColor.value = color

    // ✅ Convert color to HEX format for Firestore storage
    val hexColor = String.format("#%06X", (color.toArgb() and 0xFFFFFF))

    // ✅ Update Firestore and ViewModel
    profileViewModel.changeColor(userId, hexColor)
}
