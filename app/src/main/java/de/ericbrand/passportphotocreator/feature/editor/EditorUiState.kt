package de.ericbrand.passportphotocreator.feature.editor

import android.graphics.Bitmap
import de.ericbrand.passportphotocreator.core.model.CropTransform
import de.ericbrand.passportphotocreator.core.model.GermanPassportSpec
import de.ericbrand.passportphotocreator.core.model.PhotoSpec

data class EditorUiState(
    val imageUri: String? = null,
    val imageLoaded: Boolean = false,
    val cropTransform: CropTransform = CropTransform(),
    val photoSpec: PhotoSpec = GermanPassportSpec,
    val showGuides: Boolean = true,
    val exportReady: Boolean = false,
    val errorMessage: String? = null
)