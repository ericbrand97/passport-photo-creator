package de.ericbrand.passportphotocreator.core.model

import androidx.compose.ui.geometry.Offset

data class CropTransform(
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val offset: Offset = Offset.Zero
)