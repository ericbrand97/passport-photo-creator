package de.ericbrand.passportphotocreator.feature.editor

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderState
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
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
    sliderOffset: Offset,
    onTransformChange: (CropTransform) -> Unit,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(Size.Zero) }

    val state = rememberTransformableState { centroid, zoomChange, offsetChange, rotationChange ->
        val oldScale = transform.scale
        val newScale = max(oldScale * zoomChange, 0.1f)

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
//        Image(
//            bitmap = bitmap.asImageBitmap(),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .wrapContentSize(align = Alignment.Center)
//                .graphicsLayer {
//                    translationX = -transform.offset.x * transform.scale + sliderOffset.x
//                    translationY = -transform.offset.y * transform.scale + sliderOffset.y
//                    scaleX = transform.scale
//                    scaleY = transform.scale
//                    rotationZ = transform.rotation
//                    transformOrigin = TransformOrigin(0f, 0f)
//                }
//        )
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasSize = this.size

            val baseScale = max(canvasSize.width / bitmap.width, canvasSize.height / bitmap.height)

            val scaledBitmapSize = Size(
                width = bitmap.width * baseScale,
                height = bitmap.height * baseScale
            )

            val baseTopLeft = Offset(
                x = (canvasSize.width - scaledBitmapSize.width) / 2f,
                y = (canvasSize.height - scaledBitmapSize.height) / 2f
            )

            withTransform({
                // This matches:
                // screenPoint = scale * (rotatedBasePoint - offset)

                scale(
                    scaleX = transform.scale,
                    scaleY = transform.scale,
                    pivot = Offset.Zero
                )

                translate(
                    left = -transform.offset.x,
                    top = -transform.offset.y
                )

                rotate(
                    degrees = transform.rotation,
                    pivot = Offset.Zero
                )
            }) {
                // Base placement: keep the image centered before applying user transform.
                translate(
                    left = baseTopLeft.x,
                    top = baseTopLeft.y
                ) {
                    scale(
                        scaleX = baseScale,
                        scaleY = baseScale,
                        pivot = Offset.Zero
                    ) {
                        drawImage(bitmap.asImageBitmap())
                    }
                }
            }
        }
    }
}