package com.example.scoutquest.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapDescriptorUtils {

    suspend fun getBitmapFromUrl(context: Context, url: String): Bitmap? {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
        return result?.toBitmap()
    }

    fun Drawable.toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

    fun createCustomMarkerBitmap(context: Context, baseBitmap: Bitmap, index: Int): Bitmap {
        // Skalowanie
        val scaleFactor = 2
        val scaledWidth = baseBitmap.width * scaleFactor
        val scaledHeight = baseBitmap.height * scaleFactor
        val scaledBitmap = Bitmap.createScaledBitmap(baseBitmap, scaledWidth, scaledHeight, false)

        // index zadania
        val canvas = Canvas(scaledBitmap)
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 40f //text size
        }
        val text = index.toString()
        val x = (scaledBitmap.width - paint.measureText(text)) / 2
        val y = (scaledBitmap.height - paint.descent() - paint.ascent()) / 2
       // canvas.drawText(text, x, y, paint) //customowy tekst

        return scaledBitmap
    }

    fun Bitmap.toBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromBitmap(this)
    }

    @Composable
    fun rememberBitmapDescriptor(url: String, index: Int): BitmapDescriptor? {
        val context = LocalContext.current
        var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }

        LaunchedEffect(url, index) {
            val baseBitmap = getBitmapFromUrl(context, url)
            if (baseBitmap != null) {
                val customBitmap = createCustomMarkerBitmap(context, baseBitmap, index)
                bitmapDescriptor = customBitmap.toBitmapDescriptor()
            }
        }

        return bitmapDescriptor
    }
}