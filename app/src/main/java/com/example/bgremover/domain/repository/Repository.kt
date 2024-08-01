package com.example.bgremover.domain.repository

import com.example.bgremover.data.remote.BgRemoverApiClient
import com.example.bgremover.data.repository.ApiClient
import java.io.File

class Repository : ApiClient {
    override suspend fun removeBackground(imageFile: File): String {
        return BgRemoverApiClient.removeBackground(imageFile)
    }
}
