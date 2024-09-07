package com.example.bgremover.domain.repository

import android.content.Context
import com.example.bgremover.data.remote.BgRemoverApiClient
import com.example.bgremover.data.repository.ApiClient
import com.example.bgremover.domain.model.imageenhance.EnhanceResponse
import java.io.File

class Repository : ApiClient {
    override suspend fun removeBackground(imageFile: File): String {
        return BgRemoverApiClient.removeBackground(imageFile)
    }

    override suspend fun inhanceImage(imageUrl:String): EnhanceResponse {
        return BgRemoverApiClient.enhanceImage(imageUrl)
    }


}
