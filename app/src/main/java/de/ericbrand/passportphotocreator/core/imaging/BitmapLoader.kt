package de.ericbrand.passportphotocreator.core.imaging

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.DrawableRes
import kotlin.math.max

object BitmapLoader {

    fun loadSampledBitmapFromResource(
        context: Context,
        @DrawableRes resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeResource(context.resources, resId, boundsOptions)

        val sampleSize = calculateInSampleSize(
            width = boundsOptions.outWidth,
            height = boundsOptions.outHeight,
            reqWidth = reqWidth,
            reqHeight = reqHeight
        )

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        Log.d("BitmapLoader", "source=${boundsOptions.outWidth}x${boundsOptions.outHeight}, req=${reqWidth}x${reqHeight}, sample=$sampleSize")
        return requireNotNull(
            BitmapFactory.decodeResource(context.resources, resId, decodeOptions)
        ) { "Failed to decode drawable resource: $resId" }
    }

    fun loadSampledBitmapFromUri(
        context: Context,
        uri: android.net.Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        val resolver = context.contentResolver

        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        resolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input, null, boundsOptions)
        }

        val sampleSize = calculateInSampleSize(
            width = boundsOptions.outWidth,
            height = boundsOptions.outHeight,
            reqWidth = reqWidth,
            reqHeight = reqHeight
        )

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        return resolver.openInputStream(uri).use { input ->
            requireNotNull(
                BitmapFactory.decodeStream(input, null, decodeOptions)
            ) { "Failed to decode bitmap from URI: $uri" }
        }
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            while (
                height / (inSampleSize * 2) >= reqHeight &&
                width / (inSampleSize * 2) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }

        return max(1, inSampleSize)
    }
}