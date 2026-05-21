package de.ericbrand.passportphotocreator.feature.editor

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.ericbrand.passportphotocreator.R
import de.ericbrand.passportphotocreator.core.imaging.BitmapLoader
import de.ericbrand.passportphotocreator.core.model.CropTransform
import de.ericbrand.passportphotocreator.platform.printing.PassportPrintDocumentAdapter
import de.ericbrand.passportphotocreator.platform.printing.launchPrintJob
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
            val maxLongEdge = 1600
            val width = previewSize.width
            val height = previewSize.height
            val scale = minOf(
                1f,
                maxLongEdge / maxOf(width, height).toFloat()
            )

            previewBitmap = withContext(Dispatchers.IO) {
                BitmapLoader.loadBitmapFromUri(
                    context = context,
                    uri = state.imageUri,
                )
            }
            onAction(EditorAction.TransformChanged(CropTransform()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { stringResource(R.string.app_name) },
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
                        Icon(Icons.Outlined.PhotoLibrary, contentDescription = stringResource(R.string.pick_image_description))
                    }
                    if (state.cropTransform != CropTransform()){
                        IconButton(onClick = { onAction(EditorAction.TransformChanged(CropTransform())) }) {
                            Icon(Icons.Outlined.RestartAlt, contentDescription = stringResource(R.string.reset_transform_description))
                        }
                    }
                },
                floatingActionButton = {
                    previewBitmap?.let {
                        FloatingActionButton(
                            onClick = {
                                val safeActivity =
                                    context as? Activity ?: return@FloatingActionButton

                                launchPrintJob(
                                    activity = safeActivity,
                                    jobName = "Passport photo",
                                    adapter = PassportPrintDocumentAdapter(
                                        context = safeActivity,
                                        previewBitmap = previewBitmap!!,
                                        cropTransform = state.cropTransform,
                                        previewSize = previewSize
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Outlined.Print, stringResource(R.string.print_description))
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
                contentPadding = PaddingValues(horizontal = 20.dp)
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxSize()
                        .aspectRatio(35f / 45f)
                        .onSizeChanged { previewSize = it }
                ) {
                    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            color = backgroundColor,
                        )
                    }
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
                    } ?: run {
                        Text(
                            stringResource(R.string.select_image_user_action),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    val borderColor = MaterialTheme.colorScheme.surfaceContainerLow
                    val borderStrokeWidthPx = 5f

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            color = borderColor,
                            style = Stroke(borderStrokeWidthPx)
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Text(
                        text = stringResource(R.string.guides_label),
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
                                            Guides.NONE -> stringResource(R.string.guides_none_label)
                                            Guides.POSITION -> stringResource(R.string.guides_position_label)
                                            Guides.FACE_HEIGHT -> stringResource(R.string.guides_face_height_label)
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