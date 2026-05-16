package com.pocketstudios.feature.ai

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device background removal using ML Kit Selfie Segmentation.
 * Works in real-time at ~30fps on mid-range devices.
 * No cloud upload required.
 */
@Singleton
class BackgroundRemovalProcessor @Inject constructor() {

    private val segmenter = Segmentation.getClient(
        SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.STREAM_MODE)
            .enableRawSizeMask()
            .build()
    )

    /**
     * Remove background from a single bitmap frame.
     * Returns bitmap with background pixels set to transparent.
     */
    suspend fun removeBackground(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val inputImage = InputImage.fromBitmap(source, 0)
        val mask = Tasks.await(segmenter.process(inputImage))

        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val maskBuffer = mask.buffer
        val maskWidth = mask.width
        val maskHeight = mask.height

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                val confidence = maskBuffer.get()
                if (confidence < FOREGROUND_THRESHOLD) {
                    val scaledX = (x.toFloat() * source.width / maskWidth).toInt()
                    val scaledY = (y.toFloat() * source.height / maskHeight).toInt()
                    if (scaledX < source.width && scaledY < source.height) {
                        result.setPixel(scaledX, scaledY, Color.TRANSPARENT)
                    }
                }
            }
        }
        result
    }

    /**
     * Replace background with a custom color or image.
     */
    suspend fun replaceBackground(source: Bitmap, background: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val foreground = removeBackground(source)
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        // Draw background scaled to source size
        val scaledBg = Bitmap.createScaledBitmap(background, source.width, source.height, true)
        canvas.drawBitmap(scaledBg, 0f, 0f, null)
        // Draw foreground on top
        canvas.drawBitmap(foreground, 0f, 0f, Paint().apply { isAntiAlias = true })
        result
    }

    fun release() = segmenter.close()

    companion object {
        private const val FOREGROUND_THRESHOLD = 0.5f
    }
}
