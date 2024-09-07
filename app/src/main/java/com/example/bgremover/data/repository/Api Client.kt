package com.example.bgremover.data.repository

import android.content.Context
import com.example.bgremover.domain.model.imageenhance.ImageEnhancer
import java.io.File

interface ApiClient {

    suspend fun removeBackground(imageFile: File): String

    suspend fun enhanceImage(context: Context, imagePath: Any): ImageEnhancer
}
