package com.example.bgremover.presentation.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.bgremover.R
import com.example.bgremover.domain.usecase.ResultState
import com.example.bgremover.presentation.viewmodel.MainViewModel
import org.koin.compose.koinInject
import java.io.File

@Composable
fun BgRemover() {
    val viewModel: MainViewModel = koinInject()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var bgRemovedImageUrl by remember { mutableStateOf<String?>(null) }
    val bgRemovalState by viewModel.bgRemoval.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        bgRemovedImageUrl = null
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
                bgRemovedImageUrl = (bgRemovalState as ResultState.Success<String>).success
                Toast.makeText(context, "$bgRemovedImageUrl", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "BgRemover: $bgRemovedImageUrl")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        imageUri?.let {
            val painter = rememberImagePainter(it)
            Image(painter = painter, contentDescription = null, modifier = Modifier.size(200.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            imageFile?.let { file ->
                isLoading = true
                viewModel.removeBackground(file)
            }
        }) {
            Text("Remove Background")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            bgRemovedImageUrl?.let { url ->
                AsyncImage(model = url, contentDescription = null, modifier = Modifier.size(200.dp))
            }
        }
    }
}


