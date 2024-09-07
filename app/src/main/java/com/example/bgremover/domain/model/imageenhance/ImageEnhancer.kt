package com.example.bgremover.domain.model.imageenhance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageEnhancer(
    @SerialName("data")
    val `data`: Data,
    @SerialName("error_code")
    val errorCode: Int,
    @SerialName("error_detail")
    val errorDetail: ErrorDetail,
    @SerialName("log_id")
    val logId: String,
    @SerialName("request_id")
    val requestId: String
)