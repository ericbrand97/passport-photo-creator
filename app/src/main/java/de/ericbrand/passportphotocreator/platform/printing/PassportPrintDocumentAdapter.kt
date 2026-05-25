package de.ericbrand.passportphotocreator.platform.printing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.util.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import de.ericbrand.passportphotocreator.core.model.CropTransform
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import androidx.core.math.MathUtils.clamp
import de.ericbrand.passportphotocreator.core.model.PhotoSpec
import kotlin.math.max
import kotlin.math.min

class PassportPrintDocumentAdapter(
    private val context: Context,
    private val previewBitmap: Bitmap,
    private val cropTransform: CropTransform,
    private val previewSize: IntSize,
    private val photoSpec: PhotoSpec
) : PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {

        if (cancellationSignal?.isCanceled == true){
            callback.onLayoutCancelled()
            return
        }

        pdfDocument?.close()
        pdfDocument = PrintedPdfDocument(context, newAttributes)

        val info = PrintDocumentInfo.Builder("passport_photo.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()

        callback.onLayoutFinished(info, true)

    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {

        val document = pdfDocument ?: run{
            callback.onWriteFailed("PDF document not initialized")
            return
        }

        if (cancellationSignal?.isCanceled == true){
            callback.onWriteCancelled()
            return
        }

        try {
            val mmToPt = PT_PER_INCH / MM_PER_INCH
            val mmToPx = PRINT_DPI / MM_PER_INCH
            val page = document.startPage(0)
            val canvas = page.canvas
            val contentRect = document.pageContentRect

            canvas.drawColor(Color.WHITE)

            val photoWidthPt = photoSpec.sizeMm.width * mmToPt
            val photoHeightPt = photoSpec.sizeMm.height * mmToPt

            val photoWidthPx = (photoSpec.sizeMm.width * mmToPx).roundToInt()
            val photoHeightPx = (photoSpec.sizeMm.height * mmToPx).roundToInt()

            val padding = 10f * mmToPt

            val left = contentRect.left + (contentRect.width() - padding) / 2f - photoWidthPt
            val top = contentRect.top + (contentRect.height() - padding) / 2f - photoHeightPt
            val dstRects = arrayOfNulls<RectF>(4)

            for(i in 0..3){
                val offset = Offset((i % 2) * photoWidthPt * 1.2f, (i / 2) * photoHeightPt * 1.3f)
                dstRects[i] = RectF(
                    clamp(left + offset.x, 0f, contentRect.right - photoWidthPt),
                    top + offset.y,
                    clamp(left + photoWidthPt + offset.x, photoWidthPt, contentRect.right.toFloat()),
                    top + photoHeightPt + offset.y
                )
            }

            val finalPhotoBitmap = renderFinalPhotoBitmap(
                sourceBitmap = previewBitmap,
                transform = cropTransform,
                previewSize = previewSize,
                outSize = Size(photoWidthPx, photoHeightPx)
            )

            val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

            for(dstRect in dstRects)
                if(dstRect != null)
                    canvas.drawBitmap(finalPhotoBitmap, null, dstRect, bitmapPaint)

            document.finishPage(page)

            FileOutputStream(destination.fileDescriptor).use { output ->
                document.writeTo(output)
            }

            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: IOException) {
            callback.onWriteFailed(e.message)
        } finally {
            document.close()
            pdfDocument = null
        }

    }

    override fun onFinish() {
        pdfDocument?.close()
        pdfDocument = null
    }

    private fun renderFinalPhotoBitmap(
        sourceBitmap: Bitmap,
        transform: CropTransform,
        previewSize: IntSize,
        outSize: Size,
    ): Bitmap {
        val result = createBitmap(outSize.width, outSize.height)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        canvas.drawColor(Color.WHITE)

        val sx = outSize.width / previewSize.width.toFloat()
        val sy = outSize.height / previewSize.height.toFloat()

        val baseScale = max(previewSize.width / sourceBitmap.width.toFloat(), previewSize.height / sourceBitmap.height.toFloat())

        val scaledBitmapWidth = sourceBitmap.width * baseScale
        val scaledBitmapHeight = sourceBitmap.height * baseScale

        val baseTopLeftX = (previewSize.width - scaledBitmapWidth) / 2f
        val baseTopLeftY = (previewSize.height - scaledBitmapHeight) / 2f

        canvas.withSave {
            // Convert preview coordinates to final output coordinates.
            scale(sx, sy)

            // Same interactive transform as in the Canvas preview:
            //
            // screenPoint = scale * (rotatedBasePoint - offset)
            scale(transform.scale, transform.scale)
            translate(-transform.offset.x, -transform.offset.y)
            rotate(transform.rotation, 0f, 0f)

            // Same base placement as the preview:
            // center the bitmap, then scale it to preview width.
            translate(baseTopLeftX, baseTopLeftY)
            scale(baseScale, baseScale)

            drawBitmap(sourceBitmap, 0f, 0f, paint)
        }

        return result
    }

    private companion object {
        const val MM_PER_INCH = 25.4f
        const val PT_PER_INCH = 72f
        const val PRINT_DPI = 600f
    }
}