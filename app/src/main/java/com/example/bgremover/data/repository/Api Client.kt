package com.example.bgremover.data.repository

import android.content.Context
import com.example.bgremover.domain.model.imageenhance.EnhanceResponse
import java.io.File

interface ApiClient {

    suspend fun removeBackground(imageFile: File): String

    suspend fun inhanceImage(imageUrl:String):EnhanceResponse
}
