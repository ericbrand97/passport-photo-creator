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
    data class GuidesSelected(
        val guides: Guides
    ) : EditorAction
    data object ExportClicked : EditorAction
    data object ErrorShown : EditorAction
}