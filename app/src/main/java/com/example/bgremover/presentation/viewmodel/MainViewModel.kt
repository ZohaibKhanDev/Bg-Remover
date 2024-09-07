package com.example.bgremover.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bgremover.domain.model.imageenhance.ImageEnhancer
import com.example.bgremover.domain.repository.Repository
import com.example.bgremover.domain.usecase.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _bgRemoval = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val bgRemoval: StateFlow<ResultState<String>> = _bgRemoval.asStateFlow()


    private val _allEnhancer = MutableStateFlow<ResultState<ImageEnhancer>>(ResultState.Loading)
    val allEnhcer: StateFlow<ResultState<ImageEnhancer>> = _allEnhancer.asStateFlow()

    fun getAiEnhancer(context: Context, imagePath: Any) {
        viewModelScope.launch {
            _allEnhancer.value = ResultState.Loading
            try {
                val response = repository.enhanceImage(context, imagePath)
                _allEnhancer.value = ResultState.Success(response)
            } catch (e: Exception) {
                _allEnhancer.value = ResultState.Error(e)
            }
        }
    }

    fun removeBackground(imageFile: File) {
        viewModelScope.launch {
            _bgRemoval.value = ResultState.Loading
            try {
                val response = repository.removeBackground(imageFile)
                _bgRemoval.value = ResultState.Success(response)
            } catch (e: Exception) {
                _bgRemoval.value = ResultState.Error(e)
            }

        }

    }

}


