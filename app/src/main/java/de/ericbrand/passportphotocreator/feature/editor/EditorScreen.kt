package de.ericbrand.passportphotocreator.feature.editor

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.ericbrand.passportphotocreator.R
import de.ericbrand.passportphotocreator.core.imaging.BitmapLoader
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

    LaunchedEffect(previewSize) {
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
                BitmapLoader.loadSampledBitmapFromResource(
                    context = context,
                    resId = R.drawable.dummy_passport_photo,
                    reqWidth = reqWidth,
                    reqHeight = reqHeight
                )
            }
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { onAction(EditorAction.PickImageClicked) }
                ) {
                    Text("Pick image")
                }

                Text(
                    text = if (state.showGuides) "Guides on" else "Guides off"
                )

                Button(
                    onClick = { onAction(EditorAction.ToggleGuides) }
                ) {
                    Text("Toggle guides")
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(35f / 45f)
                        .onSizeChanged { previewSize = it }
                ) {
                    previewBitmap?.let { bitmap ->
                        CropPreview(
                            bitmap = bitmap,
                            transform = state.cropTransform,
                            onTransformChange = { newTransform ->
                                onAction(
                                    EditorAction.TransformChanged(
                                        newTransform.scale,
                                        newTransform.rotation,
                                        newTransform.offset
                                    )
                                )
                            },
                            modifier = Modifier.matchParentSize()
                        )
                    } ?: run {
                        Text("Loading preview...")
                    }
                }
            }
        }
    }
}