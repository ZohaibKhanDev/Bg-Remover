package com.example.bgremover.presentation.ui.screens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.JoinLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.JoinLeft
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
    var showImageAnimation by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

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
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Image") },
            text = { Text("Are you sure you want to delete this image?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate("bgremover")
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
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
            IconButton(onClick = { /* TODO */ }) {
                Image(imageVector = Icons.Filled.Splitscreen,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .rotate(90f)
                        .clickable { /* TODO */ })
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
                        .height(58.dp), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "", tint = Color(0XFF0766e3)
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
                        .height(58.dp), contentAlignment = Alignment.Center
                ) {
                    imageUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.deleteicon),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                showDialog = true
                            }
                            .offset(y = 5.dp)
                    )
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
                        .height(550.dp), contentAlignment = Alignment.Center
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
                                Image(
                                    painter = painterResource(id = R.drawable.transparntbg),
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color = Color.Transparent.copy(alpha = 0.30f))
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

            Spacer(modifier = Modifier.height(30.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = Color(0xFFF9F9F9),
                        shape = RoundedCornerShape(8.dp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(actionItems) { item ->
                    ActionItem(
                        icon = item.icon,
                        text = item.text,
                        isNew = item.isNew,
                        blue = item.blue,
                        lightBlue = item.lightBlue
                    )
                }
            }
        }
    }
}

data class ActionItemData(
    val icon: ImageVector,
    val text: String,
    val isNew: Boolean = false,
    val blue: Boolean = false,
    val lightBlue: Boolean = false,
)

val actionItems = listOf(
    ActionItemData(Icons.Outlined.FileDownload, "Download", false, true, false),
    ActionItemData(Icons.Outlined.FileDownload, "Download HD", false, false, true),
    ActionItemData(Icons.Outlined.Add, "Background", false, false, false),
    ActionItemData(Icons.Outlined.Brush, "Erase/Restore", false, false, false),
    ActionItemData(Icons.Outlined.JoinLeft, "Effects", isNew = true, false, false)
)

@Composable
fun ActionItem(
    icon: ImageVector,
    text: String,
    isNew: Boolean = false,
    blue: Boolean,
    lightBlue: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    color = if (blue) Color(0XFF0077ff) else if (lightBlue) Color(0XFF92b2d6) else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.TopEnd,

            ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                tint = if (blue) Color.White else if (lightBlue) Color(0XFF3589e8) else Color.Black
            )

            if (isNew) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.Yellow, shape = CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text("NEW", color = Color.Black, fontSize = 8.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )

    }
}








