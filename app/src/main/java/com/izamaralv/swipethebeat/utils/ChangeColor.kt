package com.izamaralv.swipethebeat.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardBorderColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.common.textColor
import com.izamaralv.swipethebeat.ui.theme.StandardBackground
import com.izamaralv.swipethebeat.ui.theme.StandardCard
import com.izamaralv.swipethebeat.ui.theme.yellowHighContrastColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

fun changeColor(color: Color, userId: String, profileViewModel: ProfileViewModel) {
    Log.d("ProfileViewModel", "🔄 Updating profile color to: $color")
    if (color == yellowHighContrastColor) {
        softComponentColor.value = color
        textColor.value = color
        cardColor.value = Color.Black
        cardBorderColor.value = color
        backgroundColor.value = Color.Black

    } else {
        // ✅ Apply color change globally
        softComponentColor.value = color
        textColor.value = Color.White
        cardColor.value = StandardCard
        cardBorderColor.value = StandardCard
        backgroundColor.value = StandardBackground
    }
        // ✅ Convert color to HEX format for Firestore storage
        val hexColor = String.format("#%06X", (color.toArgb() and 0xFFFFFF))

        // ✅ Update Firestore and ViewModel
        profileViewModel.changeColor(userId, hexColor)
}