package de.ericbrand.passportphotocreator.feature.editor

import de.ericbrand.passportphotocreator.core.model.CropTransform

sealed interface EditorAction {
    data object PickImageClicked : EditorAction
    data class ImageSelected(val uri: String) : EditorAction
    data class TransformChanged(
        val scale: Float,
        val offsetX: Float,
        val offsetY: Float,
        val rotation: Float
    ) : EditorAction
    data object ToggleGuides : EditorAction
    data object ExportClicked : EditorAction
    data object ErrorShown : EditorAction
}