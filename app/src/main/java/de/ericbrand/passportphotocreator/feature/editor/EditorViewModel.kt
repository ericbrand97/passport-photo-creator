package de.ericbrand.passportphotocreator.feature.editor

import androidx.lifecycle.ViewModel
import de.ericbrand.passportphotocreator.core.model.CropTransform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EditorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState

    fun onAction(action: EditorAction){
        when(action){
            is EditorAction.ImageSelected -> {
                _uiState.update {
                    it.copy(
                        imageUri = action.uri,
                        imageLoaded = true,
                        exportReady = true,
                        errorMessage = null,
                        showGuides = Guides.POSITION
                    )
                }
            }

            is EditorAction.TransformChanged -> {
                _uiState.update {
                    it.copy(
                        cropTransform = action.transform
                    )
                }
            }

            is EditorAction.SliderOffsetChanged -> {
                _uiState.update {
                    it.copy(
                        sliderOffset = action.sliderOffset
                    )
                }
            }

            is EditorAction.GuidesSelected -> {
                _uiState.update {
                    it.copy(showGuides = action.guides)
                }
            }

            EditorAction.ExportClicked -> {
                // call exporter
            }

            EditorAction.PickImageClicked -> Unit

            EditorAction.ErrorShown -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }
}