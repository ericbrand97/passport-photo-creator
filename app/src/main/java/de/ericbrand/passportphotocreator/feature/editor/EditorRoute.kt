package de.ericbrand.passportphotocreator.feature.editor

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

    EditorScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)

            if (action is EditorAction.ExportClicked && state.exportReady) {
                onNavigateToExport()
            }
        }
    )
}