package de.ericbrand.passportphotocreator.feature.editor

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArtTrack
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.ericbrand.passportphotocreator.core.imaging.BitmapLoader
import de.ericbrand.passportphotocreator.core.model.CropTransform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    state: EditorUiState,
    onAction: (EditorAction) -> Unit
) {
    val context = LocalContext.current

    var previewSize by remember { mutableStateOf(IntSize.Zero) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(previewSize, state.imageUri) {
        if (previewSize.width > 0 && previewSize.height > 0) {
            // Optional cap so the interactive preview stays lightweight
            val maxLongEdge = 1600
            val width = previewSize.width
            val height = previewSize.height
            val scale = minOf(
                1f,
                maxLongEdge / maxOf(width, height).toFloat()
            )
            val reqWidth = (width * scale).roundToInt()
            val reqHeight = (height * scale).roundToInt()

            previewBitmap = withContext(Dispatchers.IO) {
                BitmapLoader.loadSampledBitmapFromUri(
                    context = context,
                    uri = state.imageUri,
                    reqWidth = reqWidth,
                    reqHeight = reqHeight
                )
            }
            onAction(EditorAction.TransformChanged(CropTransform()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Passport Photo Creator") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { onAction(EditorAction.PickImageClicked) }) {
                        Icon(Icons.Outlined.PhotoLibrary, contentDescription = "Pick image")
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .aspectRatio(35f / 45f)
                        .onSizeChanged { previewSize = it }
                ) {
                    previewBitmap?.let { bitmap ->
                        CropPreview(
                            bitmap = bitmap,
                            transform = state.cropTransform,
                            sliderOffset = state.sliderOffset,
                            onTransformChange = { newTransform ->
                                onAction(
                                    EditorAction.TransformChanged(newTransform)
                                )
                            },
                            modifier = Modifier.matchParentSize()
                        )

                        if (state.showGuides == Guides.POSITION) {
                            PositionOverlay()
                        }

                        else if (state.showGuides == Guides.FACE_HEIGHT) {
                            FaceHeightOverlay()
                        }

                        val borderColor = MaterialTheme.colorScheme.surfaceContainerLow
                        val borderStrokeWidthPx = 5f

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                color = borderColor,
                                style = Stroke(borderStrokeWidthPx)
                            )
                        }
                    } ?: run {
                        Text("Loading preview...")
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Text(
                        text = "Guides",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics { heading() }
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Guides.entries.forEachIndexed { index, option ->
                            SegmentedButton(
                                onClick = { onAction(EditorAction.GuidesSelected(option)) },
                                selected = state.showGuides == option,
                                shape = SegmentedButtonDefaults.itemShape(index, Guides.entries.size),
                                label = {
                                    Text(
                                        when (option) {
                                            Guides.NONE -> "None"
                                            Guides.POSITION -> "Position"
                                            Guides.FACE_HEIGHT -> "Face height"
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}