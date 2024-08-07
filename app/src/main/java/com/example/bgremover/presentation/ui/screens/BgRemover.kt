package com.example.bgremover.presentation.ui.screens

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.navigation.NavController
import com.example.bgremover.R
import com.example.bgremover.createNotificationChannel
import com.example.bgremover.domain.usecase.ResultState
import com.example.bgremover.presentation.ui.navigation.Screens
import com.example.bgremover.presentation.viewmodel.MainViewModel
import org.koin.compose.koinInject
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BgRemover(navController: NavController) {
    val viewModel: MainViewModel = koinInject()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var bgRemovedImageBase64 by remember { mutableStateOf<String?>(null) }
    val bgRemovalState by viewModel.bgRemoval.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        bgRemovedImageBase64 = null
        imageFile = uri?.let {
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
                    bgRemovedImageBase64?.let {imagebg->
                        navController.navigate(
                            Screens.BgDetail.route + "/${Uri.encode(it.toString())}/${
                                Uri.encode(imagebg )
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
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "",
                modifier = Modifier.height(39.dp)
            )
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



@SuppressLint("ServiceCast")
fun saveImage(bitmap: Bitmap?, context: Context, isHd: Boolean) {
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
        put(MediaStore.MediaColumns.DISPLAY_NAME, if (isHd) "bg_removed_image_hd.png" else "bg_removed_image.png")
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
            scaledBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            Toast.makeText(context, "Image saved to Pictures", Toast.LENGTH_SHORT).show()
            notificationBuilder.setContentText("Download complete").setProgress(0, 0, false).setOngoing(false)
            notificationManager.notify(1, notificationBuilder.build())
        } ?: run {
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
            notificationBuilder.setContentText("Download failed").setProgress(0, 0, false).setOngoing(false)
            notificationManager.notify(1, notificationBuilder.build())
        }
    } ?: run {
        Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
        notificationBuilder.setContentText("Download failed").setProgress(0, 0, false).setOngoing(false)
        notificationManager.notify(1, notificationBuilder.build())
    }
}