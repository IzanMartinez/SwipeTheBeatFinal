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

    // comprobamos si es el tema de alto contraste
    if (color == yellowHighContrastColor) {
        softComponentColor.value = color
        textColor.value = color
        cardColor.value = Color.Black
        cardBorderColor.value = color
        backgroundColor.value = Color.Black

    } else {
        softComponentColor.value = color
        textColor.value = Color.White
        cardColor.value = StandardCard
        cardBorderColor.value = StandardCard
        backgroundColor.value = StandardBackground
    }
        // COnvertimos el color a hexadecimal para firestore
        val hexColor = String.format("#%06X", (color.toArgb() and 0xFFFFFF))
        Log.d("ProfileViewModel", "✅ Profile color applied: $hexColor")

        profileViewModel.changeColorInFirebase(userId, hexColor)
}