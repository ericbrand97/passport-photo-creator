package de.ericbrand.passportphotocreator.core.model

data class PhotoSpec(
    val widthMm: Float,
    val heightMm: Float,
    val faceHeightMinMm: Float? = null,
    val faceHeightMaxMm: Float? = null
)

val GermanPassportSpec = PhotoSpec(
    35f,
    45f,
    32f,
    36f
)