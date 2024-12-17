package com.izamaralv.swipethebeat.common

import androidx.compose.runtime.mutableStateOf
import com.izamaralv.swipethebeat.ui.theme.StandardBackground
import com.izamaralv.swipethebeat.ui.theme.StandardCard
import com.izamaralv.swipethebeat.ui.theme.StandardField

// Variables de colores que almacenan el estado mutable
var backgroundColor = mutableStateOf(StandardBackground) // Color de fondo estándar
var cardColor = mutableStateOf(StandardCard) // Color de las tarjetas estándar
var softComponentColor = mutableStateOf(StandardField) // Color de los componentes suaves
