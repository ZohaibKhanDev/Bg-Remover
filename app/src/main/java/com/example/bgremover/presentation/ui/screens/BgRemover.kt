package com.example.bgremover.presentation.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.bgremover.presentation.viewmodel.MainViewModel
import org.koin.compose.koinInject

@Composable
fun Bg_Remover() {
    val viewModel: MainViewModel = koinInject()
    var isLoading by remember {
        mutableStateOf(false)
    }
}