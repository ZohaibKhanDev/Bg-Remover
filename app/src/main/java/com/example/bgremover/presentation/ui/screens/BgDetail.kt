package com.example.bgremover.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.JoinLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
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
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BgDetail(
    navController: NavController, imageUrl: String?, bgremoveimage: String?
) {
    var showBgRemovedImage by remember { mutableStateOf(false) }
    var showphoto by remember { mutableStateOf(false) }
    var showColor by remember { mutableStateOf(false) }
    var addBg by remember { mutableStateOf(false) }
    var showImageAnimation by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color.Transparent) }

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
            IconButton(onClick = { showBgRemovedImage = !showBgRemovedImage }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        ),
                    contentAlignment = Alignment.Center
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
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(selectedColor.copy(alpha = 0.30f))
                                )
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(400.dp, 550.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                )

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
                Spacer(modifier = Modifier.height(18.dp)) // Add space before the card
                // Ensure to no space once not showing the card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                            Text(text = "Reset", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = "Done",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Blue.copy(alpha = 0.60f)
                            )
                        }
                        if (showColor) {
                            LazyHorizontalGrid(
                                rows = GridCells.Fixed(2),
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                        Color(0xFFFFF0F5),
                                        Color(0xFFFFE4C4)
                                    )
                                ) { color ->

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .width(55.dp)
                                            .height(65.dp)
                                            .background(color)
                                            .clickable {
                                                selectedColor = color
                                            }
                                    )

                                }
                            }
                        }

                        if (showphoto) {
                            Text(text = "There is Nothing Photos")
                        } else {
                            Text(text = "There is Nothing Photos")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = { showphoto = true }) {
                                Text(text = "Photo")
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(onClick = { showColor = true }) {
                                Text(text = "Color")
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(18.dp)) // No need to handle showing of card while addBg
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                                .background(Color(0XFF0077ff))
                                .clickable {

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
                                .clickable {},
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
                                .size(45.dp)
                                .clickable {
                                    addBg = !addBg
                                }, contentAlignment = Alignment.TopEnd
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
                            text = "Add",
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
                                imageVector = Icons.Outlined.Brush,
                                contentDescription = "Erase/Restore",
                                modifier = Modifier
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






