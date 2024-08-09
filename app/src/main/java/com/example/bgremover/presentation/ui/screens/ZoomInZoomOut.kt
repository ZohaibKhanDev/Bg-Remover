package com.example.bgremover.presentation.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView

@Composable
fun ZoomableImage(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    onBitmapCaptured: (Bitmap) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current.density
    val imageModifier = Modifier.graphicsLayer(
        scaleX = scale,
        scaleY = scale,
        translationX = offset.x,
        translationY = offset.y
    )

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale *= zoom
                offset = Offset(
                    offset.x + pan.x,
                    offset.y + pan.y
                )
            }
        }
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = imageModifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    val view = LocalView.current
    LaunchedEffect(Unit) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        onBitmapCaptured(bitmap)
    }
}



