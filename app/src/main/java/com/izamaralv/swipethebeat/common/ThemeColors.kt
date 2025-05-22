package com.izamaralv.swipethebeat.common

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.izamaralv.swipethebeat.ui.theme.StandardBackground
import com.izamaralv.swipethebeat.ui.theme.StandardCard
import com.izamaralv.swipethebeat.ui.theme.StandardField

// ✅ Declare mutable state variables
var backgroundColor = mutableStateOf(StandardBackground) // Color de fondo estándar
var cardColor = mutableStateOf(StandardCard) // Color de las tarjetas estándar
var softComponentColor = mutableStateOf(StandardField) // Color de los componentes suaves
var textColor = mutableStateOf(Color.White) // Color del texto
var cardBorderColor = mutableStateOf(StandardCard) // Color del borde de las tarjetas
var basicBorder = mutableStateOf(Color.Black) // Color del borde básico


