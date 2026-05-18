package de.ericbrand.passportphotocreator.core.model

data class CropTransform(
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f
)