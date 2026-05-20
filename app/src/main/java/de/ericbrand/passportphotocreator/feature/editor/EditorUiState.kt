package de.ericbrand.passportphotocreator.feature.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import de.ericbrand.passportphotocreator.core.model.CropTransform
import de.ericbrand.passportphotocreator.core.model.GermanPassportSpec
import de.ericbrand.passportphotocreator.core.model.PhotoSpec

enum class Guides{
    NONE,
    POSITION,
    FACE_HEIGHT
}

data class EditorUiState(
    val imageUri: Uri? = null,
    val imageLoaded: Boolean = false,
    val cropTransform: CropTransform = CropTransform(),
    val sliderOffset: Offset = Offset.Zero,
    val photoSpec: PhotoSpec = GermanPassportSpec,
    val showGuides: Guides = Guides.NONE,
    val exportReady: Boolean = false,
    val errorMessage: String? = null
)