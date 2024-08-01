package com.example.scoutquest.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

suspend fun getBitmapFromUrl(context: Context, url: String): Bitmap? {
    val imageLoader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false) // Disable hardware bitmaps.
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

fun createCustomMarkerBitmap(context: Context, baseBitmap: Bitmap, number: Int): Bitmap {
    // Scale the base bitmap to a larger size
    val scaledWidth = baseBitmap.width * 3 // Adjust the scale factor as needed
    val scaledHeight = baseBitmap.height * 3 // Adjust the scale factor as needed
    val scaledBitmap = Bitmap.createScaledBitmap(baseBitmap, scaledWidth, scaledHeight, false)

    val canvas = Canvas(scaledBitmap)
    val textColor = Color.BLACK
    var textSize = 96f // Adjust text size as needed
    val strokeColor = Color.WHITE
    var strokeWidth = 12f // Adjust stroke width as needed

    val paint = Paint().apply {
        color = textColor
        textSize = textSize
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val strokePaint = Paint(paint).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidth
        color = strokeColor
    }

    val xPos = (canvas.width / 2).toFloat()
    val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

    canvas.drawText(number.toString(), xPos, yPos, strokePaint)
    canvas.drawText(number.toString(), xPos, yPos, paint)

    return scaledBitmap
}

fun Bitmap.toBitmapDescriptor(): BitmapDescriptor {
    return BitmapDescriptorFactory.fromBitmap(this)
}

@Composable
fun rememberBitmapDescriptor(url: String, number: Int): BitmapDescriptor? {
    val context = LocalContext.current
    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }

    LaunchedEffect(url, number) {
        val baseBitmap = getBitmapFromUrl(context, url)
        if (baseBitmap != null) {
            val customBitmap = createCustomMarkerBitmap(context, baseBitmap, number)
            bitmapDescriptor = customBitmap.toBitmapDescriptor()
        }
    }

    return bitmapDescriptor
}