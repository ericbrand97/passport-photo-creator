package de.ericbrand.passportphotocreator.feature.editor

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import de.ericbrand.passportphotocreator.core.model.CropTransform
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private fun Offset.rotateBy(degrees: Float): Offset {
    val radians = Math.toRadians(degrees.toDouble())
    val cos = cos(radians).toFloat()
    val sin = sin(radians).toFloat()
    return Offset(
        x = x * cos - y * sin,
        y = x * sin + y * cos
    )
}

@Composable
fun CropPreview(
    bitmap: Bitmap,
    transform: CropTransform,
    onTransformChange: (CropTransform) -> Unit,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(Size.Zero) }

    val state = rememberTransformableState { centroid, zoomChange, offsetChange, rotationChange ->
        val oldScale = transform.scale
        val newScale = max(oldScale * zoomChange, 1f)

        val effectiveCentroid =
            centroid.takeIf { it.isSpecified } ?: size.center

        val newOffset =
            (transform.offset + effectiveCentroid / oldScale).rotateBy(rotationChange) -
                    (effectiveCentroid / newScale + offsetChange / oldScale)

        onTransformChange(
            transform.copy(
                scale = newScale,
                rotation = transform.rotation + rotationChange,
                offset = newOffset
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .onSizeChanged { size = it.toSize() }
            .transformable(state = state)
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center)
                .graphicsLayer {
                    translationX = -transform.offset.x * transform.scale
                    translationY = -transform.offset.y * transform.scale
                    scaleX = transform.scale
                    scaleY = transform.scale
                    rotationZ = transform.rotation
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        )
    }
}