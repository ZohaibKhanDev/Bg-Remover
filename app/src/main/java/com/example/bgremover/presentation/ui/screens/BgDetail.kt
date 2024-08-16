package com.example.bgremover.presentation.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.DecodeUtils.calculateInSampleSize
import com.example.bgremover.R
import com.example.bgremover.presentation.ui.navigation.Screens
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "InvalidColorHexValue")
@Composable
fun BgDetail(
    navController: NavController, imageUrl: String?, bgremoveimage: String?
) {
    var showBgRemovedImage by remember { mutableStateOf(false) }
    var selectedGallery by remember { mutableStateOf<Bitmap?>(null) }
    var blurRadius by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    var brush by remember {
        mutableStateOf(false)
    }
    var showPhoto by remember { mutableStateOf(true) }
    var showColor by remember { mutableStateOf(false) }
    var addBg by remember { mutableStateOf(false) }
    var showImageAnimation by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var restore by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color.Transparent) }
    var isMagicBrushEnabled by remember { mutableStateOf(false) }
    var isMore by remember {
        mutableStateOf(false)
    }
    var switch by remember {
        mutableStateOf(false)
    }

    var switch1 by remember {
        mutableStateOf(false)
    }

    var effect by remember {
        mutableStateOf(false)
    }

    var interstitialAd: InterstitialAd? by remember { mutableStateOf(null) }
    var selectedPhoto by remember { mutableStateOf<Int?>(null) }

    var slider1 by remember { mutableStateOf(0f) }

    InterstitialAd.load(context,
        "ca-app-pub-3940256099942544/1033173712",
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
                Log.d("ADD", "onAdFailedToLoad: True")
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d("ADD", "onAdLoaded: True")
            }
        })

    val density = LocalDensity.current.density
    var slider by remember { mutableStateOf(0f) }
    var split by remember {
        mutableStateOf(false)
    }
    var isBackgroundRemoved by remember { mutableStateOf(false) }
        val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
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

    var brushSize by remember { mutableStateOf(100.dp) }
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
            IconButton(onClick = {
                split = !split
                showImageAnimation = split
                showBgRemovedImage = !split
            }) {
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
            IconButton(onClick = { isMore = !isMore }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = ""
                )

                DropdownMenu(expanded = isMore, onDismissRequest = { isMore = false }) {
                    DropdownMenuItem(text = {
                        Text(text = "Log Out")
                    }, onClick = {
                        navController.navigate(Screens.BgRemover.route)
                    })
                }

            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }) {
        val vertical = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(vertical)
                .height(if (addBg) 1130.dp else 1050.dp)
                .fillMaxWidth()
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
                            var scale by remember { mutableStateOf(1f) }
                            var offset by remember { mutableStateOf(Offset.Zero) }

                            Box(
                                modifier = Modifier
                                    .size(400.dp, 550.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .then(
                                            if (switch) Modifier.blur(radius = slider.dp) else Modifier
                                        )
                                ) {

                                    when {
                                        selectedPhoto != null -> {
                                            Image(
                                                painter = painterResource(id = selectedPhoto!!),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        selectedGallery != null -> {
                                            Image(
                                                bitmap = selectedGallery!!.asImageBitmap(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        else -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.transparntbg),
                                                contentDescription = "",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(11.dp))
                                                    .fillMaxSize()
                                                    .background(selectedColor)
                                                    .graphicsLayer {
                                                        alpha = slider1
                                                    }
                                            )
                                        }
                                    }
                                }


                                if (isBackgroundRemoved) {
                                    bgremoveimage?.let { base64 ->
                                        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                                        val bitmap = BitmapFactory.decodeByteArray(
                                            imageBytes, 0, imageBytes.size
                                        )

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
                                                .fillMaxSize()
                                        )
                                        var pointerOffset by remember {
                                            mutableStateOf(Offset(0f, 0f))
                                        }

                                        if (isBackgroundRemoved) {
                                            Box {
                                                Canvas(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .pointerInput("dragging") {
                                                            detectDragGestures { change, dragAmount ->
                                                                pointerOffset += dragAmount
                                                            }
                                                        }
                                                        .drawWithContent {
                                                            drawContent()
                                                            drawRect(
                                                                Brush.radialGradient(
                                                                    listOf(
                                                                        Color.Yellow,
                                                                        Color.Transparent
                                                                    ),
                                                                    center = pointerOffset,
                                                                    radius = brushSize.toPx()
                                                                )
                                                            )
                                                        }
                                                ) {

                                                }
                                            }
                                        }
                                    }
                                } else {

                                    if (restore){
                                        imageUrl?.let {
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
                                        }
                                    }else{
                                        bgremoveimage?.let { base64 ->
                                            val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                                            val bitmap = BitmapFactory.decodeByteArray(
                                                imageBytes, 0, imageBytes.size
                                            )

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
                                                    .fillMaxSize()
                                            )

                                        }
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
                                    Text(text = "Reset",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable {
                                            showColor = false
                                            showPhoto = true
                                            selectedColor = Color.Transparent
                                            selectedPhoto = null
                                            selectedGallery = null
                                        })
                                    Text(text = "Done",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Blue.copy(alpha = 0.60f),
                                        modifier = Modifier.clickable {
                                            addBg = false
                                        })
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
                                                    selectedGallery = null
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
                                            Box(modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .width(60.dp)
                                                .height(50.dp)
                                                .clickable {
                                                    selectedPhoto = photoResId
                                                }) {
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
                                        }, colors = ButtonDefaults.buttonColors(
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
                                        }, colors = ButtonDefaults.buttonColors(
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
                if (effect) {
                    LazyColumn {
                        item {
                            TextButton(
                                onClick = { effect = false },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Done", color = Color.Blue, modifier = Modifier
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                colors = CardDefaults.cardColors(Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Blur background",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Switch(
                                            checked = switch,
                                            onCheckedChange = { switch = it },
                                            colors = SwitchDefaults.colors(
                                                checkedTrackColor = Color(0XFF976d00),
                                                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.50f),
                                                uncheckedThumbColor = Color.White,
                                                checkedBorderColor = Color.Transparent,
                                                uncheckedBorderColor = Color.Transparent
                                            )
                                        )
                                    }


                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = "Blur amount", fontSize = 15.sp)
                                        Slider(
                                            value = slider,
                                            onValueChange = { newValue ->
                                                slider = newValue
                                                blurRadius = newValue * 10
                                            },
                                            valueRange = 0f..10f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            colors = SliderDefaults.colors(
                                                thumbColor = if (switch) Color(0XFF976d00) else Color.LightGray,
                                                activeTrackColor = if (switch) Color(0XFF976d00) else Color.LightGray
                                            )
                                        )

                                    }


                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Add Shadow")
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Switch(
                                            checked = switch1,
                                            onCheckedChange = { switch1 = it },
                                            colors = SwitchDefaults.colors(
                                                checkedTrackColor = Color(0XFF976d00),
                                                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.50f),
                                                uncheckedThumbColor = Color.White,
                                                checkedBorderColor = Color.Transparent,
                                                uncheckedBorderColor = Color.Transparent
                                            )
                                        )
                                        Text(
                                            text = "Beta",
                                            modifier = Modifier
                                                .padding(start = 4.dp)
                                                .clickable { },
                                            style = TextStyle(color = Color.Blue)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = "Opacity", fontSize = 15.sp)
                                        Slider(
                                            value = if (switch1) slider1.coerceIn(1f, 100f) else 0f,
                                            onValueChange = {
                                                if (switch1) slider1 = it.coerceIn(1f, 100f)
                                            },
                                            valueRange = 0f..100f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            colors = SliderDefaults.colors(
                                                thumbColor = if (switch1) Color(0XFF976d00) else Color.LightGray,
                                                activeTrackColor = if (switch1) Color(0XFF976d00) else Color.LightGray
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (brush) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(16.dp)
                            ) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Reset",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.clickable {

                                                isBackgroundRemoved = false
                                                showBgRemovedImage = !restore
                                            }
                                        )

                                        Text(
                                            text = "Done",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF03A9F4),
                                            modifier = Modifier.clickable {
                                                brush = false
                                                isBackgroundRemoved=false
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                isBackgroundRemoved = true
                                                restore=false
                                            },
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(43.dp),
                                            shape = RoundedCornerShape(7.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0XFF6e4c03)
                                            )
                                        ) {
                                            Text(text = "Erase")
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                restore = !restore
                                                showImageAnimation = restore
                                                showBgRemovedImage = !restore

                                                isBackgroundRemoved = false
                                            },
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(43.dp),
                                            shape = RoundedCornerShape(7.dp),
                                        ) {
                                            Text(text = "Restore")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Brush Size",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Slider(
                                            value = brushSize.value,
                                            onValueChange = {
                                                brushSize = it.dp
                                            },
                                            valueRange = 0f..300f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color(0xFFFFC107),
                                                activeTrackColor = Color(0xFF8D6E63)
                                            ),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Magic Brush",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Switch(
                                            checked = isMagicBrushEnabled,
                                            onCheckedChange = { isMagicBrushEnabled = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color(0xFFFFC107),
                                            )
                                        )
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
                                            detectTapGestures(onLongPress = { })
                                        }) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(45.dp)
                                            .background(Color(0XFF0077ff))
                                            .clickable {
                                                interstitialAd?.show(context as Activity)
                                                bgremoveimage?.let { base64 ->
                                                    val imageBytes =
                                                        Base64.decode(base64, Base64.DEFAULT)
                                                    val bitmap = BitmapFactory.decodeByteArray(
                                                        imageBytes, 0, imageBytes.size
                                                    )
                                                    saveImage(
                                                        bitmap,
                                                        context = context,
                                                        false,
                                                        selectedColor,
                                                        selectedPhoto,
                                                        galleryBitmap = selectedGallery,
                                                        blurRadius
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
                                        text = "Download",
                                        fontSize = 12.sp,
                                        color = Color(0XFF0077ff)
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
                                                interstitialAd?.show(context as Activity)

                                                bgremoveimage?.let { base64 ->
                                                    val imageBytes =
                                                        Base64.decode(base64, Base64.DEFAULT)
                                                    val bitmap = BitmapFactory.decodeByteArray(
                                                        imageBytes, 0, imageBytes.size
                                                    )

                                                    saveImage(
                                                        bitmap,
                                                        context = context,
                                                        true,
                                                        selectedColor,
                                                        selectedPhoto,
                                                        galleryBitmap = selectedGallery,
                                                        blurRadius
                                                    )
                                                }

                                            }, contentAlignment = Alignment.TopEnd
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
                                        text = "DownloadHd",
                                        fontSize = 12.sp,
                                        color = Color(0XFF0077ff)
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
                                    Box(modifier = Modifier
                                        .size(45.dp).clip(CircleShape)
                                        .clickable {
                                            brush = !brush
                                        }
                                        .background(
                                            Color.Transparent, shape = RoundedCornerShape(50)
                                        ), contentAlignment = Alignment.TopEnd) {
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
                                            .size(45.dp).clip(CircleShape)
                                            .clickable {
                                                effect = !effect
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
                                            .size(45.dp).clip(CircleShape)
                                            .clickable {
                                                val imageUrl = "$bgremoveimage"
                                                val encodedImageUrl = Uri.encode(imageUrl)
                                                val canvaUrl =
                                                    "https://www.canva.com/create/design?upload=$encodedImageUrl"

                                                val intent =
                                                    Intent(Intent.ACTION_VIEW, Uri.parse(canvaUrl))
                                                intent.setPackage("com.android.chrome")

                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: ActivityNotFoundException) {
                                                    e.printStackTrace()

                                                    val fallbackIntent = Intent(
                                                        Intent.ACTION_VIEW, Uri.parse(canvaUrl)
                                                    )
                                                    try {
                                                        context.startActivity(fallbackIntent)
                                                    } catch (e: Exception) {
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "No browser found to open the link",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
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
    }
}




