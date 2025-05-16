package com.izamaralv.swipethebeat.common

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.izamaralv.swipethebeat.ui.theme.StandardBackground
import com.izamaralv.swipethebeat.ui.theme.StandardCard
import com.izamaralv.swipethebeat.ui.theme.StandardField
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import androidx.core.graphics.toColorInt

// ✅ Declare mutable state variables
var backgroundColor = mutableStateOf(StandardBackground) // Color de fondo estándar
var cardColor = mutableStateOf(StandardCard) // Color de las tarjetas estándar
var softComponentColor = mutableStateOf(StandardField) // Color de los componentes suaves


