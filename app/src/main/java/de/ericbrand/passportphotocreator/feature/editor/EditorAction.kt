package de.ericbrand.passportphotocreator.feature.editor

import androidx.compose.ui.geometry.Offset

sealed interface EditorAction {
    data object PickImageClicked : EditorAction
    data class ImageSelected(val uri: String) : EditorAction
    data class TransformChanged(
        val scale: Float,
        val rotation: Float,
        val offset: Offset
    ) : EditorAction
    data object ToggleGuides : EditorAction
    data object ExportClicked : EditorAction
    data object ErrorShown : EditorAction
}