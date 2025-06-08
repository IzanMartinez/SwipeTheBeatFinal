package com.izamaralv.swipethebeat.common

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.izamaralv.swipethebeat.ui.theme.StandardBackground
import com.izamaralv.swipethebeat.ui.theme.StandardCard
import com.izamaralv.swipethebeat.ui.theme.StandardField

// Estas variables controlan los colores dinámicos de la UI. Se usan a lo largo de la app para mantener una
// apariencia coherente y permitir cambios en tiempo real.

// Color principal de fondo de la pantalla
var backgroundColor = mutableStateOf(StandardBackground)

// Color default para las tarjetas de contenido
var cardColor = mutableStateOf(StandardCard)

// Color para elementos suaves como campos de texto y botones secundarios
var softComponentColor = mutableStateOf(StandardField)

// Color predeterminado del texto en la UI
var textColor = mutableStateOf(Color.White)

// Color del borde en las tarjetas (coincide con el cardColor excepto en el tema de accesibilidad)
var cardBorderColor = mutableStateOf(StandardCard)
