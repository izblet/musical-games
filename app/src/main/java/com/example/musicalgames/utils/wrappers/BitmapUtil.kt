package com.example.musicalgames.utils.wrappers

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object BitmapUtil {
    //decodes a resource at the largest power-of-two downsample that keeps both dimensions
    //at or above reqSize - avoids decoding oversized source images (which can exceed the
    //canvas's max texture size) when the bitmap is only ever drawn at icon size
    fun decodeSampledBitmap(resources: Resources, resId: Int, reqSize: Int): Bitmap {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, resId, options)

        var inSampleSize = 1
        while (options.outWidth / (inSampleSize * 2) >= reqSize && options.outHeight / (inSampleSize * 2) >= reqSize) {
            inSampleSize *= 2
        }

        return BitmapFactory.decodeResource(resources, resId, BitmapFactory.Options().apply { this.inSampleSize = inSampleSize })
    }
}
