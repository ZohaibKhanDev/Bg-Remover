package com.example.bgremover.presentation.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.bgremover.R
import com.example.bgremover.createNotificationChannel
import com.example.bgremover.domain.usecase.ResultState
import com.example.bgremover.presentation.ui.navigation.Screens
import com.example.bgremover.presentation.viewmodel.MainViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import org.koin.compose.koinInject
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BgRemover(navController: NavController) {
    val context = LocalContext.current
    val viewModel: MainViewModel = koinInject()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isMore by remember { mutableStateOf(false) }
    var bgRemovedImageBase64 by remember { mutableStateOf<String?>(null) }
    val bgRemovalState by viewModel.bgRemoval.collectAsState()


    var rewardedAd: RewardedAd? = null
    RewardedAd.load(
        context,
        "ca-app-pub-3940256099942544/5224354917",
        AdRequest.Builder().build(),
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                rewardedAd = null
            }

            override fun onAdLoaded(p0: RewardedAd) {
                super.onAdLoaded(p0)
                rewardedAd = p0
            }
        }
    )


    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdClicked() {
            super.onAdClicked()
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
        }

        override fun onAdImpression() {
            super.onAdImpression()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            bgRemovedImageBase64 = null
            imageFile = it.let {
                val inputStream = context.contentResolver.openInputStream(it)
                inputStream?.let { stream ->
                    val file = File(context.cacheDir, "selected_image.png")
                    file.outputStream().use { output ->
                        stream.copyTo(output)
                    }
                    file
                }
            }
            imageFile?.let { file ->
                isLoading = true
                viewModel.removeBackground(file)
                rewardedAd?.show(context as Activity, OnUserEarnedRewardListener {
                })
            }
        }
    }

    LaunchedEffect(bgRemovalState) {
        when (bgRemovalState) {
            is ResultState.Error -> {
                isLoading = false
                val error = (bgRemovalState as ResultState.Error).error
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

            ResultState.Loading -> {
                isLoading = true


            }

            is ResultState.Success -> {
                isLoading = false
                bgRemovedImageBase64 = (bgRemovalState as ResultState.Success<String>).success
                imageUri?.let {
                    bgRemovedImageBase64?.let { imagebg ->
                        navController.navigate(
                            Screens.BgDetail.route + "/${Uri.encode(it.toString())}/${
                                Uri.encode(imagebg)
                            }"
                        )
                    }

                }
            }
        }
    }


    Scaffold(topBar = {
        TopAppBar(title = {
            Row {
                Text(
                    text = "remover",
                    color = Color(0XFF454545),
                    fontWeight = FontWeight.Medium,
                    fontSize = 27.sp
                )
                Text(
                    text = "bg",
                    color = Color(0XFFbbc1c5),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }, navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.bgremover),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(60.dp),
            )
        }, actions = {
            IconButton(onClick = { isMore = !isMore }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = "",
                )

                DropdownMenu(expanded = isMore, onDismissRequest = { isMore = false }) {
                    DropdownMenuItem(text = {
                        Text(text = "Log In")
                    }, onClick = {
                        isMore = false
                        Toast.makeText(context, "Please Create New Account", Toast.LENGTH_SHORT)
                            .show()
                    })
                }

            }
        })
    }) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(45.dp))

            Image(
                painter = painterResource(id = R.drawable.targets),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Upload an image to",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0XFF454545)
            )

            Text(
                text = "remove the",
                color = Color(0XFF454545),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "background",
                color = Color(0XFF454545),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0e78e3)),
                modifier = Modifier
                    .width(230.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = "Upload Image",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            Text(
                text = "No image? Try one of these::",
                color = Color(0XFF596671),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 93.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.women),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .width(50.dp)
                        .height(45.dp),
                    contentScale = ContentScale.Crop
                )

                Image(
                    painter = painterResource(id = R.drawable.cat),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .width(50.dp)
                        .height(45.dp),
                    contentScale = ContentScale.Crop
                )

                Image(
                    painter = painterResource(id = R.drawable.car),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .width(50.dp)
                        .height(45.dp),
                    contentScale = ContentScale.Crop
                )

                Image(
                    painter = painterResource(id = R.drawable.phone),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .width(50.dp)
                        .height(45.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    append("By uploading an image or URL you agree to our ")
                    pushStringAnnotation(tag = "URL", annotation = "https://www.example.com/terms")
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = Color(0XFF0e78e3)
                        )
                    ) {
                        append("Terms of Service")
                    }
                    pop()
                    append(". To learn more about how remove.bg handles your personal data, check our ")
                    pushStringAnnotation(
                        tag = "URL",
                        annotation = "https://www.example.com/privacy"
                    )
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = Color(0XFF0e78e3)
                        )
                    ) {
                        append("Privacy Policy")
                    }
                    pop()
                    append(".")
                },
                fontSize = 11.sp,
                color = Color(0XFF68747d),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


fun compositeBackground(
    bitmap: Bitmap?,
    backgroundColor: Color?,
    backgroundImage: Bitmap?
): Bitmap {
    val width = bitmap?.width ?: 1
    val height = bitmap?.height ?: 1
    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(resultBitmap)

    backgroundImage?.let {
        val scaledBackground = Bitmap.createScaledBitmap(it, width, height, true)
        canvas.drawBitmap(scaledBackground, 0f, 0f, null)
    }

    backgroundColor?.let {
        val paint = Paint().apply {
            color = it.toArgb()
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    bitmap?.let {
        canvas.drawBitmap(it, 0f, 0f, null)
    }

    return resultBitmap
}


fun getBitmapFromDrawable(context: Context, drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    return drawable?.let {
        Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
        }
    }
}


fun applyBlurToBitmap(bitmap: Bitmap, radius: Float, context: Context): Bitmap {
    val validRadius = radius.coerceIn(1f, 25f)
    val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val renderScript = RenderScript.create(context)
    val input = Allocation.createFromBitmap(renderScript, bitmap)
    val output = Allocation.createFromBitmap(renderScript, blurredBitmap)
    val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    script.setRadius(validRadius)
    script.setInput(input)
    script.forEach(output)
    output.copyTo(blurredBitmap)
    renderScript.destroy()
    return blurredBitmap
}


fun saveImage(
    bitmap: Bitmap?,
    context: Context,
    isHd: Boolean,
    backgroundColor: Color?,
    backgroundImageId: Int?,
    galleryBitmap: Bitmap?,
    blurRadius: Float
) {
    createNotificationChannel(context)

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationBuilder = NotificationCompat.Builder(context, "DOWNLOAD_CHANNEL")
        .setSmallIcon(R.drawable.baseline_download_24)
        .setContentTitle("Image Download")
        .setContentText("Downloading...")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)

    notificationManager.notify(1, notificationBuilder.build())

    val contentValues = ContentValues().apply {
        put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            if (isHd) "bg_removed_image_hd.png" else "bg_removed_image.png"
        )
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->

            val scaledBitmap = if (isHd) {
                bitmap?.let { Bitmap.createScaledBitmap(it, it.width * 2, it.height * 2, true) }
            } else {
                bitmap
            }


            val backgroundImageBitmap = galleryBitmap ?: backgroundImageId?.let { getBitmapFromDrawable(context, it) }
            val finalBackgroundBitmap = if (blurRadius > 0 && backgroundImageBitmap != null) {
                val clampedBlurRadius = blurRadius.coerceIn(1f, 25f) // Clamp blur radius
                applyBlurToBitmap(backgroundImageBitmap, clampedBlurRadius, context)
            } else {
                backgroundImageBitmap
            }


            val finalBitmap = compositeBackground(scaledBitmap, backgroundColor, finalBackgroundBitmap)


            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            Toast.makeText(context, "Image saved to Pictures", Toast.LENGTH_SHORT).show()
            notificationBuilder.setContentText("Download complete").setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1, notificationBuilder.build())
        } ?: run {
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
            notificationBuilder.setContentText("Download failed").setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1, notificationBuilder.build())
        }
    } ?: run {
        Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
        notificationBuilder.setContentText("Download failed").setProgress(0, 0, false)
            .setOngoing(false)
        notificationManager.notify(1, notificationBuilder.build())
    }
}
