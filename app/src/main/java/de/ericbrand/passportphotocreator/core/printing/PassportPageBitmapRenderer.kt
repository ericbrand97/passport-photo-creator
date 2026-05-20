package de.ericbrand.passportphotocreator.core.printing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.createBitmap

object PassportPageBitmapRenderer{

    fun renderTestPage(contentRect: Rect): Bitmap {

        val bitmap = createBitmap(contentRect.width(), contentRect.height())

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 48f
            isAntiAlias = true
        }

        canvas.drawText("Passport print test", 40f, 80f, paint)

        return bitmap

    }

}