package com.example.bgremover

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bgremover.di.appModule
import com.example.bgremover.presentation.ui.screens.BgRemover
import com.example.bgremover.ui.theme.BgRemoverTheme
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startKoin {
            androidContext(this@MainActivity)
            androidLogger()
            modules(appModule)
        }
        setContent {
            BgRemoverTheme {
                BgRemover()
            }
        }
    }
}


@Composable
fun BackgroundRemoverApp() {
    val context = LocalContext.current
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                originalBitmap = bitmap
                processedBitmap = null
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        (processedBitmap ?: originalBitmap)?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit  // Change this to ContentScale.Fit or ContentScale.Inside
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "Pick Image", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            originalBitmap?.let { bitmap ->
                BackgroundRemover.bitmapForProcessing(
                    bitmap,
                    true,
                    object : OnBackgroundChangeListener {
                        override fun onSuccess(bitmap: Bitmap) {
                            processedBitmap = bitmap
                        }

                        override fun onFailed(exception: Exception) {
                            // Handle the exception
                        }
                    }
                )
            }
        }) {
            Text(text = "Remove Background", fontSize = 16.sp)
        }
    }
}



fun resizeBitmapTo4K(bitmap: Bitmap): Bitmap {
    val width = 3840
    val height = 2160
    val scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val scaleX = width.toFloat() / bitmap.width
    val scaleY = height.toFloat() / bitmap.height
    val scale = Math.max(scaleX, scaleY)

    val scaledWidth = scale * bitmap.width
    val scaledHeight = scale * bitmap.height

    val left = (width - scaledWidth) / 2
    val top = (height - scaledHeight) / 2

    val canvas = Canvas(scaledBitmap)
    val matrix = Matrix()
    matrix.postScale(scale, scale)
    matrix.postTranslate(left, top)
    canvas.drawBitmap(bitmap, matrix, null)

    return scaledBitmap
}