package com.example.bgremover.data.repository

import java.io.File

interface ApiClient {

    suspend fun removeBackground(imageFile: File): String
    
}
