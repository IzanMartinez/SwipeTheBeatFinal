package com.izamaralv.swipethebeat.common

import androidx.compose.runtime.mutableStateOf
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.ui.theme.StandardBackground
import com.izamaralv.swipethebeat.ui.theme.StandardCard
import com.izamaralv.swipethebeat.ui.theme.StandardComponent
import com.izamaralv.swipethebeat.ui.theme.StandardField

var backgroundColor = mutableStateOf(StandardBackground)
var cardColor = mutableStateOf(StandardCard)
var hardComponentColor = mutableStateOf(StandardComponent)
var softComponentColor = mutableStateOf(StandardField)
var textColor = mutableStateOf(StandardField)
var borderLogo = mutableStateOf(R.drawable.border_logo_green)
var logo = mutableStateOf(R.drawable.logo_green)