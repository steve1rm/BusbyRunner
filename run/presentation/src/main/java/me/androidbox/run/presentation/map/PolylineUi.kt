package me.androidbox.run.presentation.map

import androidx.compose.ui.graphics.Color
import me.androidbox.core.domain.location.Location

data class PolylineUi(
    val locationSrc: Location,
    val locationDst: Location,
    val color: Color
)

