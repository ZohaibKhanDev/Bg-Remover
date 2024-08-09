package com.example.bgremover.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


