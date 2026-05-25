package de.ericbrand.passportphotocreator.feature.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.core.math.MathUtils.clamp
import de.ericbrand.passportphotocreator.core.model.PhotoSpec

@Composable
fun PositionOverlay(
    modifier: Modifier = Modifier,
    photoSpec: PhotoSpec
){
    val guideColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.fillMaxSize()) {
        val w: Float = size.width
        val h: Float = size.height

        val mmToPx = w / photoSpec.sizeMm.width

        val strokeWidthPx = 1f
        val noseFillAlpha = 0.3f
        val eyesFillAlpha = 0.2f
        val strokeAlpha = 0.7f

        if (photoSpec.nosePositionXRangeMm != null) {

            val min = photoSpec.nosePositionXRangeMm.start
            val max = photoSpec.nosePositionXRangeMm.endInclusive

            drawRect(
                color = guideColor,
                alpha = noseFillAlpha,
                topLeft = Offset(min * mmToPx, 0f),
                size = Size((max - min) * mmToPx, h)
            )

            drawRect(
                color = guideColor,
                alpha = strokeAlpha,
                topLeft = Offset(min * mmToPx, 0f),
                size = Size((max - min) * mmToPx, h),
                style = Stroke(strokeWidthPx)
            )

        }

        if (photoSpec.eyePositionYRangeMm != null) {

            val min = photoSpec.eyePositionYRangeMm.start
            val max = photoSpec.eyePositionYRangeMm.endInclusive

            drawRect(
                color = guideColor,
                alpha = eyesFillAlpha,
                topLeft = Offset(0f, min * mmToPx),
                size = Size(w, (max - min) * mmToPx)
            )

            drawRect(
                color = guideColor,
                alpha = strokeAlpha,
                topLeft = Offset(0f, min * mmToPx),
                size = Size(w, (max - min) * mmToPx),
                style = Stroke(strokeWidthPx)
            )

        }

        if (photoSpec.faceCentered){

            drawLine(
                color = guideColor,
                alpha = strokeAlpha,
                start = Offset(0f, h / 2f),
                end = Offset(w, h / 2f),
                strokeWidth = strokeWidthPx
            )

            drawLine(
                color = guideColor,
                alpha = strokeAlpha,
                start = Offset(w / 2f, 0f),
                end = Offset(w / 2f, h),
                strokeWidth = strokeWidthPx
            )

        }
    }
}

@Composable
fun FaceHeightOverlay(
    modifier: Modifier = Modifier,
    photoSpec: PhotoSpec
){
    val guideColor = MaterialTheme.colorScheme.primary
    var offsetY by remember { mutableFloatStateOf(0f) }

    var canvasWidth by remember { mutableIntStateOf(0) }

    val maxFaceHeight = photoSpec.faceHeightToleranceRangeMm?.endInclusive ?: photoSpec.faceHeightRangeMm.endInclusive

    Canvas(
        modifier = modifier.fillMaxSize()
            .clip(RectangleShape)
            .onSizeChanged { canvasWidth = it.width }
            .pointerInput(canvasWidth){
                if (!photoSpec.faceCentered) {
                    val minFaceHeight = photoSpec.faceHeightToleranceRangeMm?.start ?: photoSpec.faceHeightRangeMm.start
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (canvasWidth > 0) {
                                val mmToPx = canvasWidth / photoSpec.sizeMm.width
                                offsetY = clamp(
                                    offsetY + dragAmount.y,
                                    (minFaceHeight - maxFaceHeight) * mmToPx,
                                    (photoSpec.sizeMm.height - maxFaceHeight) * mmToPx
                                )
                            }
                        }
                    )
                }
            }
    ){
        val w: Float = size.width

        val mmToPx = w / photoSpec.sizeMm.width

        val strokeWidthPx = 1.dp.toPx()
        val fillAlpha = 0.3f
        val strokeAlpha = 0.7f

        val faceHeightRangeOffset: Float

        if (photoSpec.faceHeightToleranceRangeMm != null) {

            val start = photoSpec.faceHeightToleranceRangeMm.start
            val end = photoSpec.faceHeightToleranceRangeMm.endInclusive

            faceHeightRangeOffset = end - photoSpec.faceHeightRangeMm.endInclusive

            drawRect(
                color = guideColor,
                alpha = fillAlpha,
                topLeft = Offset(0f, 0f + offsetY),
                size = Size(w, (end - start) * mmToPx)
            )

            drawRect(
                color = guideColor,
                alpha = strokeAlpha,
                topLeft = Offset(0f, 0f + offsetY),
                size = Size(w, (end - start) * mmToPx),
                style = Stroke(strokeWidthPx)
            )

        } else {
            faceHeightRangeOffset = 0f
        }

        val start = photoSpec.faceHeightRangeMm.start
        val end = photoSpec.faceHeightRangeMm.endInclusive

        drawRect(
            color = guideColor,
            alpha = fillAlpha,
            topLeft = Offset(0f, faceHeightRangeOffset * mmToPx + offsetY),
            size = Size(w, (end - start) * mmToPx)
        )

        drawRect(
            color = guideColor,
            alpha = strokeAlpha,
            topLeft = Offset(0f, faceHeightRangeOffset * mmToPx + offsetY),
            size = Size(w, (end - start) * mmToPx),
            style = Stroke(strokeWidthPx)
        )

        drawLine(
            color = guideColor,
            alpha = strokeAlpha,
            start = Offset(0f, maxFaceHeight * mmToPx + offsetY),
            end = Offset(w, maxFaceHeight * mmToPx + offsetY),
            strokeWidth = strokeWidthPx
        )

    }
}

@Composable
fun CombinedOverlay(
    modifier: Modifier = Modifier,
    photoSpec: PhotoSpec
){
    val guideColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.fillMaxSize()) {
        val w: Float = size.width
        val h: Float = size.height

        val mmToPx = w / photoSpec.sizeMm.width

        val strokeWidthPx = 1f
        val fillAlpha = 0.3f
        val strokeAlpha = 0.7f

        if (photoSpec.faceCentered) {

            drawLine(
                color = guideColor,
                alpha = strokeAlpha,
                start = Offset(0f, h / 2f),
                end = Offset(w, h / 2f),
                strokeWidth = strokeWidthPx
            )

            drawLine(
                color = guideColor,
                alpha = strokeAlpha,
                start = Offset(w / 2f, 0f),
                end = Offset(w / 2f, h),
                strokeWidth = strokeWidthPx
            )
        }

        val min = photoSpec.faceHeightRangeMm.start
        val max = photoSpec.faceHeightRangeMm.endInclusive

        drawRect(
            color = guideColor,
            alpha = fillAlpha,
            topLeft = Offset(0f, (h - max * mmToPx) / 2f),
            size = Size(w, (max - min) / 2 * mmToPx),
        )

        drawRect(
            color = guideColor,
            alpha = strokeAlpha,
            topLeft = Offset(0f, (h - max * mmToPx) / 2f),
            size = Size(w, (max - min) / 2 * mmToPx),
            style = Stroke(strokeWidthPx)
        )

        drawRect(
            color = guideColor,
            alpha = fillAlpha,
            topLeft = Offset(0f,  (h + min * mmToPx) / 2f),
            size = Size(w, (max - min) / 2 * mmToPx),
        )

        drawRect(
            color = guideColor,
            alpha = strokeAlpha,
            topLeft = Offset(0f, (h + min * mmToPx) / 2f),
            size = Size(w, (max - min) / 2 * mmToPx),
            style = Stroke(strokeWidthPx)
        )

        if(photoSpec.faceHeightToleranceRangeMm != null){

            val minTolerance = photoSpec.faceHeightToleranceRangeMm.start
            val maxTolerance = photoSpec.faceHeightToleranceRangeMm.endInclusive

            drawRect(
                color = guideColor,
                alpha = fillAlpha,
                topLeft = Offset(0f, (h - maxTolerance * mmToPx) / 2f),
                size = Size(w, (maxTolerance - minTolerance) / 2 * mmToPx)
            )

            drawRect(
                color = guideColor,
                alpha = strokeAlpha,
                topLeft = Offset(0f, (h - maxTolerance * mmToPx) / 2f),
                size = Size(w, (maxTolerance - minTolerance) / 2 * mmToPx),
                style = Stroke(strokeWidthPx)
            )

            drawRect(
                color = guideColor,
                alpha = fillAlpha,
                topLeft = Offset(0f, (h + minTolerance * mmToPx) / 2f),
                size = Size(w, (maxTolerance - minTolerance) / 2 * mmToPx)
            )

            drawRect(
                color = guideColor,
                alpha = strokeAlpha,
                topLeft = Offset(0f, (h + minTolerance * mmToPx) / 2f),
                size = Size(w, (maxTolerance - minTolerance) / 2 * mmToPx),
                style = Stroke(strokeWidthPx)
            )
        }

    }
}