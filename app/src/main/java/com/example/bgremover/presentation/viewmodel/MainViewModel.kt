package com.example.bgremover.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bgremover.domain.model.imageenhance.EnhanceResponse
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


    private val _allEnhancer = MutableStateFlow<ResultState<EnhanceResponse>>(ResultState.Loading)
    val allEnhcer: StateFlow<ResultState<EnhanceResponse>> = _allEnhancer.asStateFlow()

    fun getAiEnhancer(imageUrl:String) {
        viewModelScope.launch {
            _allEnhancer.value = ResultState.Loading
            try {
                val response = repository.inhanceImage(imageUrl)
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


