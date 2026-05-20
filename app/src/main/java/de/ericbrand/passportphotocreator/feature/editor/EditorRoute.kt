package de.ericbrand.passportphotocreator.feature.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditorRoute(
    viewModel: EditorViewModel = viewModel(),
    onNavigateToExport: () -> Unit
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null)
            viewModel.onAction(EditorAction.ImageSelected(uri))
    }

    EditorScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)

            when (action) {
                EditorAction.PickImageClicked -> {
                    pickImageLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
                EditorAction.ExportClicked -> {
                    if (state.exportReady)
                        onNavigateToExport()
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}