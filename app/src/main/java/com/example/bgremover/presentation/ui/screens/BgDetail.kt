package com.example.bgremover.presentation.ui.screens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BgDetail(
    navController: NavController,
    imageUrl: String?,
    bgremoveimage: String?
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var showBgRemovedImage by remember { mutableStateOf(false) }
    var split by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(3000) // Delay before switching to the next image
        showBgRemovedImage = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.bgremover),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(60.dp),
                    )
                },
                actions = {

                    IconButton(onClick = { /* TODO */ }) {
                        Image(
                            imageVector = Icons.Filled.Splitscreen,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .rotate(90f)
                                .clickable { split = !split }
                        )
                    }

                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = ""
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = ""
                        )
                    }

                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = ""
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Crossfade(targetState = showBgRemovedImage) { isBgRemoved ->
                if (isBgRemoved) {
                    bgremoveimage?.let { base64 ->
                        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        Column {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
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
                } else {
                    imageUrl?.let {
                        Box(
                            modifier = Modifier
                                .width(340.dp)
                                .border(
                                    BorderStroke(2.dp, color = Color.DarkGray),
                                    shape = RoundedCornerShape(11.dp)
                                )
                                .height(450.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(11.dp)),
                                contentScale = ContentScale.Crop,
                                onSuccess = { isLoading = false },
                                onError = { isLoading = false }
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.DarkGray.copy(alpha = 0.30f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.targets),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.TopStart)
                                        .padding(10.dp)
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.targets),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.Center)
                                        .padding(10.dp)
                                )


                                Image(
                                    painter = painterResource(id = R.drawable.targets),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.BottomEnd)
                                        .padding(10.dp)
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.targets),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.CenterStart)
                                        .padding(10.dp)
                                )
                            }
                        }
                    } ?: run {
                        Text(
                            text = "No image selected",
                            color = Color.Red,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}





