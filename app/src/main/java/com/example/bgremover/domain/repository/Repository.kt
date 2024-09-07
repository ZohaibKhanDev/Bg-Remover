package com.example.bgremover.domain.repository

import android.content.Context
import com.example.bgremover.data.remote.BgRemoverApiClient
import com.example.bgremover.data.repository.ApiClient
import com.example.bgremover.domain.model.imageenhance.ImageEnhancer
import java.io.File

class Repository : ApiClient {
    override suspend fun removeBackground(imageFile: File): String {
        return BgRemoverApiClient.removeBackground(imageFile)
    }

    override suspend fun enhanceImage(context: Context, imagePath: Any): ImageEnhancer {
        return BgRemoverApiClient.enhanceImage(context, imagePath)
    }

}
