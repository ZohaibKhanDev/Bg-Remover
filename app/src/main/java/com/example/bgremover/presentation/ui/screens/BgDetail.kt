package com.example.bgremover.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.JoinLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bgremover.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "InvalidColorHexValue")
@Composable
fun BgDetail(
    navController: NavController, imageUrl: String?, bgremoveimage: String?
) {
    var showBgRemovedImage by remember { mutableStateOf(false) }
    var showPhoto by remember { mutableStateOf(true) }
    var showColor by remember { mutableStateOf(false) }
    var addBg by remember { mutableStateOf(false) }
    var showImageAnimation by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color.Transparent) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isPressing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var selectedPhoto by remember { mutableStateOf<Int?>(null) }
    var selectedGallery by remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val selectedBitmap = BitmapFactory.decodeStream(inputStream)
                selectedBitmap?.let { bitmap ->
                    selectedPhoto = null
                    selectedGallery = bitmap
                }
            }
        }
    )






    LaunchedEffect(Unit) {
        delay(3000)
        showBgRemovedImage = true
        showImageAnimation = false
    }

    val animatedScale = animateFloatAsState(
        targetValue = if (showImageAnimation) 1f else 0f, animationSpec = tween(
            durationMillis = 1500, easing = LinearOutSlowInEasing
        ), label = ""
    )

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text("Delete Image") },
            text = { Text("Are you sure you want to delete this image?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("bgremover")
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            })
    }

    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.bgremover),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(60.dp),
            )
        }, actions = {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Filled.Splitscreen, contentDescription = "")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.Undo, contentDescription = ""
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.Redo, contentDescription = ""
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = ""
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }) {
        val vertical = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(vertical)
                .fillMaxSize()
                .height(if (addBg) 1030.dp else 900.dp)
                .background(Color.White)
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 40.dp, start = 11.dp),
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .width(58.dp)
                        .background(color = Color(0XFFb5cef7).copy(alpha = 0.55f))
                        .height(58.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "",
                        tint = Color(0XFF0766e3)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .border(
                            BorderStroke(3.dp, color = Color(0XFF0377fc)),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(color = Color(0XFFb5cef7).copy(alpha = 0.55f))
                        .height(58.dp),
                    contentAlignment = Alignment.Center
                ) {
                    imageUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Image(painter = painterResource(id = R.drawable.deleteicon),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                showDialog = true
                            }
                            .offset(y = 5.dp))
                }
            }

            imageUrl?.let {
                Box(
                    modifier = Modifier
                        .width(400.dp)
                        .border(
                            BorderStroke(2.dp, color = Color.LightGray.copy(alpha = 0.50f)),
                            shape = RoundedCornerShape(11.dp)
                        )
                        .height(550.dp)
                        .background(
                            if (selectedColor != Color.Transparent) selectedColor
                            else Color.Transparent
                        ), contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showImageAnimation,
                        enter = fadeIn(animationSpec = tween(durationMillis = 1500)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 1500))
                    ) {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier
                                .size(400.dp, 550.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .scale(animatedScale.value),
                            contentScale = ContentScale.Crop
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = showBgRemovedImage,
                        enter = slideInHorizontally(initialOffsetX = { it }),
                        exit = slideOutHorizontally(targetOffsetX = { -it })
                    ) {
                        bgremoveimage?.let { base64 ->
                            val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                            val bitmap =
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            Box(
                                modifier = Modifier
                                    .size(400.dp, 550.dp)
                                    .clip(RoundedCornerShape(11.dp)),
                                contentAlignment = Alignment.Center
                            ) {

                                if (selectedPhoto != null) {
                                    Image(
                                        painter = painterResource(id = selectedPhoto!!),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    if (selectedGallery != null) {
                                        Image(
                                            bitmap = selectedGallery!!.asImageBitmap(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.transparntbg),
                                            contentDescription = "",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        selectedColor?.let { color ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(11.dp))
                                                    .fillMaxSize()
                                                    .background(color)
                                            )
                                        }
                                    }
                                }

                                bgremoveimage?.let { base64 ->
                                    val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                                    val bitmap = BitmapFactory.decodeByteArray(
                                        imageBytes,
                                        0,
                                        imageBytes.size
                                    )

                                    Box(
                                        modifier = Modifier.pointerInput(Unit) {
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
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .graphicsLayer(
                                                    scaleX = scale,
                                                    scaleY = scale,
                                                    translationX = offset.x,
                                                    translationY = offset.y
                                                )
                                                .fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }

                        } ?: run {
                            Text(
                                text = "No background removed image available",
                                color = Color.Red,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } ?: run {
                Text(
                    text = "Image Not Detected",
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (addBg) {

            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            if (addBg) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 11.dp, end = 11.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Reset",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable {
                                            showColor = false
                                            showPhoto = true
                                            selectedColor
                                            selectedPhoto = null
                                        }
                                    )
                                    Text(
                                        text = "Done",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Blue.copy(alpha = 0.60f),
                                        modifier = Modifier.clickable {
                                            addBg = false
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(11.dp))


                                if (showColor) {
                                    LazyHorizontalGrid(
                                        rows = GridCells.Fixed(2),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(135.dp)
                                            .padding(top = 7.dp),
                                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        items(
                                            listOf(
                                                Color(0xFFFFFFFF),
                                                Color(0xFF000000),
                                                Color(0xFFFF0000),
                                                Color(0xFF00FF00),
                                                Color(0xFF0000FF),
                                                Color(0xFFFFFF00),
                                                Color(0xFFFFA500),
                                                Color(0xFF800080),
                                                Color(0xFF00FFFF),
                                                Color(0xFFFFC0CB),
                                                Color(0xFFA52A2A),
                                                Color(0xFF808080),
                                                Color(0xFFFF6347),
                                                Color(0xFF4682B4),
                                                Color(0xFFD2691E),
                                                Color(0xFF7FFF00),
                                                Color(0xFFDC143C),
                                                Color(0xFFBDB76B),
                                                Color(0xFF556B2F),
                                                Color(0xFFFF8C00),
                                                Color(0xFF9932CC),
                                                Color(0xFFE9967A),
                                                Color(0xFF8FBC8F),
                                                Color(0xFF483D8B),
                                                Color(0xFF2F4F4F),
                                                Color(0xFF00CED1),
                                                Color(0xFF9400D3),
                                                Color(0xFFFF1493),
                                                Color(0xFF00BFFF),
                                                Color(0xFF696969),
                                                Color(0xFF1E90FF),
                                                Color(0xFFB22222),
                                                Color(0xFFFFFAF0),
                                                Color(0xFF228B22),
                                                Color(0xFFFFD700),
                                                Color(0xFFDAA520),
                                                Color(0xFFADFF2F),
                                                Color(0xFFF0FFF0),
                                                Color(0xFFFF69B4),
                                                Color(0xFFCD5C5C),
                                                Color(0xFF4B0082),
                                                Color(0xFFF8756F0F5),
                                                Color(0xFFFF098F5),
                                                Color(0xFFFF6745F5),
                                                Color(0XFF835720533)
                                            )
                                        ) { color ->
                                            Box(modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .width(60.dp)
                                                .height(50.dp)
                                                .background(color)
                                                .clickable {
                                                    selectedPhoto = null
                                                    selectedColor = color
                                                })
                                        }
                                    }
                                } else if (showPhoto) {
                                    LazyHorizontalGrid(
                                        rows = GridCells.Fixed(2),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(135.dp)
                                            .padding(top = 7.dp),
                                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {

                                        items(1) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .width(60.dp)
                                                    .height(50.dp)
                                                    .clickable {
                                                        launcher.launch("image/*")
                                                    }, contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }

                                        }
                                        items(
                                            listOf(
                                                R.drawable.car,
                                                R.drawable.cat,
                                                R.drawable.women,
                                                R.drawable.phone,
                                            )
                                        ) { photoResId ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .width(60.dp)
                                                    .height(50.dp)
                                                    .clickable {
                                                        selectedPhoto = photoResId
                                                    }
                                            ) {
                                                Image(
                                                    painter = painterResource(id = photoResId),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
                                    }

                                }

                                Spacer(modifier = Modifier.height(7.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            showPhoto = true
                                            showColor = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            Color.LightGray.copy(alpha = 0.40f)
                                        )
                                    ) {
                                        Text(text = "Photo", color = Color.Black)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Button(
                                        onClick = {
                                            showPhoto = false
                                            showColor = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            Color.LightGray.copy(alpha = 0.40f)
                                        )
                                    ) {
                                        Text(text = "Color", color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        val context = LocalContext.current
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = { /* Trigger tooltip */ })
                                }) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(45.dp)
                                    .background(Color(0XFF0077ff))
                                    .clickable {
                                        bgremoveimage?.let { base64 ->
                                            val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                                            val bitmap =
                                                BitmapFactory.decodeByteArray(
                                                    imageBytes,
                                                    0,
                                                    imageBytes.size
                                                )
                                            saveImage(
                                                bitmap,
                                                context = context,
                                                false,
                                                selectedColor,
                                                selectedPhoto,
                                                galleryBitmap = selectedGallery
                                            )
                                        }
                                    }, contentAlignment = Alignment.TopEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FileDownload,
                                    contentDescription = "Download",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = "Download", fontSize = 12.sp, color = Color(0XFF0077ff)
                            )
                        }
                    }

                    item {
                        val context = LocalContext.current

                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = { /* Trigger tooltip */ })
                                }) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(45.dp)
                                    .background(Color(0XFFc1dff5))
                                    .clickable {
                                        bgremoveimage?.let { base64 ->
                                            val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                                            val bitmap =
                                                BitmapFactory.decodeByteArray(
                                                    imageBytes,
                                                    0,
                                                    imageBytes.size
                                                )

                                            saveImage(
                                                bitmap,
                                                context = context,
                                                true,
                                                selectedColor,
                                                selectedPhoto,
                                                galleryBitmap = selectedGallery
                                            )
                                        }

                                    },
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FileDownload,
                                    contentDescription = "DownloadHd",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                    tint = Color.Blue
                                )
                            }
                            Text(
                                text = "DownloadHd", fontSize = 12.sp, color = Color(0XFF0077ff)
                            )
                        }
                    }

                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = { /* Trigger tooltip */ })
                                }) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(45.dp)
                                    .clickable {
                                        addBg = true
                                    },
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                )
                            }
                            Text(
                                text = "Add", fontSize = 12.sp,
                            )
                        }
                    }


                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = { /* Trigger tooltip */ })
                                }) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                                    .clickable {

                                    }, contentAlignment = Alignment.TopEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Brush,
                                    contentDescription = "Erase/Restore",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                )
                            }
                            Text(
                                text = "Erase/Restore",
                                fontSize = 12.sp,
                            )
                        }
                    }

                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = { /* Trigger tooltip */ })
                                }) {

                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clickable {

                                    }, contentAlignment = Alignment.TopEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.JoinLeft,
                                    contentDescription = "Effects",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                )
                            }
                            Text(
                                text = "Effects",
                                fontSize = 12.sp,
                            )
                        }
                    }

                    item {
                        val context = LocalContext.current
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = { /* Trigger tooltip */ })
                                }) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clickable {
                                        Intent(Intent.ACTION_MAIN).also {
                                            it.`package` = "com.canva.editor"
                                            try {
                                                context.startActivity(it)
                                            } catch (e: ActivityNotFoundException) {
                                                e.printStackTrace()
                                                val playStoreIntent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("https://play.google.com/store/apps/details?id=com.canva.editor")
                                                )
                                                context.startActivity(playStoreIntent)
                                            }
                                        }
                                    }, contentAlignment = Alignment.TopEnd
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.canva),
                                    contentDescription = "Canva",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                )
                            }
                            Text(
                                text = "Canva",
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}





