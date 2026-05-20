package de.ericbrand.passportphotocreator.feature.editor

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import de.ericbrand.passportphotocreator.core.model.CropTransform

sealed interface EditorAction {
    data object PickImageClicked : EditorAction
    data class ImageSelected(val uri: Uri) : EditorAction
    data class TransformChanged(
        val transform: CropTransform
    ) : EditorAction
    data class SliderOffsetChanged(
        val sliderOffset: Offset
    ) : EditorAction
    data class GuidesSelected(
        val guides: Guides
    ) : EditorAction
    data object ExportClicked : EditorAction
    data object ErrorShown : EditorAction
}