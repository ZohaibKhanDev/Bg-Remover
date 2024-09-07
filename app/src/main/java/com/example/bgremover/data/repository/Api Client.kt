package com.example.bgremover.data.repository

import com.example.bgremover.domain.model.imageenhance.ImageEnhancer
import java.io.File

interface ApiClient {

    suspend fun removeBackground(imageFile: File): String

    suspend fun enhanceImage(imageFile: File): ImageEnhancer
}
