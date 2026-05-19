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

@Composable
fun PositionOverlay(
    modifier: Modifier = Modifier
){
    val guideColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.fillMaxSize()) {
        val w: Float = size.width
        val h: Float = size.height

        val mmToPx = w / 35f

        val strokeWidthPx = 1f
        val noseFillAlpha = 0.3f
        val eyesFillAlpha = 0.2f
        val strokeAlpha = 0.7f

        drawRect(
            color = guideColor,
            alpha = noseFillAlpha,
            topLeft = Offset(w / 2f - 1.5f * mmToPx, 0f),
            size = Size(3f * mmToPx, h)
        )

        drawRect(
            color = guideColor,
            alpha = strokeAlpha,
            topLeft = Offset(w / 2f - 1.5f * mmToPx, 0f),
            size = Size(3f * mmToPx, h),
            style = Stroke(strokeWidthPx)
        )

        drawRect(
            color = guideColor,
            alpha = eyesFillAlpha,
            topLeft = Offset(0f, 13.5f * mmToPx),
            size = Size(w, 9f * mmToPx)
        )

        drawRect(
            color = guideColor,
            alpha = strokeAlpha,
            topLeft = Offset(0f, 13.5f * mmToPx),
            size = Size(w, 9f * mmToPx),
            style = Stroke(strokeWidthPx)
        )
    }
}

@Composable
fun FaceHeightOverlay(
    modifier: Modifier = Modifier
){
    val guideColor = MaterialTheme.colorScheme.primary
    var offsetY by remember { mutableFloatStateOf(0f) }

    var canvasWidth by remember { mutableIntStateOf(0) }

    Canvas(
        modifier = modifier.fillMaxSize()
            .clip(RectangleShape)
            .onSizeChanged { canvasWidth = it.width }
            .pointerInput(canvasWidth){
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (canvasWidth > 0) {
                            val mmToPx = canvasWidth / 35f
                            offsetY = clamp(offsetY + dragAmount.y, -12f * mmToPx, 5f * mmToPx)
                        }
                    }
                )
            }
    ){
        val w: Float = size.width

        val mmToPx = w / 35f

        val strokeWidthPx = 1.dp.toPx()
        val fillAlpha = 0.3f
        val strokeAlpha = 0.7f

        drawRect(
            color = guideColor,
            alpha = fillAlpha,
            topLeft = Offset(0f, 0f + offsetY),
            size = Size(w, 12f * mmToPx)
        )

        drawRect(
            color = guideColor,
            alpha = strokeAlpha,
            topLeft = Offset(0f, 0f + offsetY),
            size = Size(w, 12f * mmToPx),
            style = Stroke(strokeWidthPx)
        )

        drawRect(
            color = guideColor,
            alpha = fillAlpha,
            topLeft = Offset(0f, 4f * mmToPx + offsetY),
            size = Size(w, 4f * mmToPx)
        )

        drawRect(
            color = guideColor,
            alpha = strokeAlpha,
            topLeft = Offset(0f, 4f * mmToPx + offsetY),
            size = Size(w, 4f * mmToPx),
            style = Stroke(strokeWidthPx)
        )

        drawLine(
            color = guideColor,
            alpha = strokeAlpha,
            start = Offset(0f, 40f * mmToPx + offsetY),
            end = Offset(w, 40f * mmToPx + offsetY),
            strokeWidth = strokeWidthPx
        )

    }
}