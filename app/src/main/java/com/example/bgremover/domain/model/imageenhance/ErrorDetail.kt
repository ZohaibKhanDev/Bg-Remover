package com.example.bgremover.domain.model.imageenhance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorDetail(
    @SerialName("code")
    val code: String,
    @SerialName("code_message")
    val codeMessage: String,
    @SerialName("message")
    val message: String,
    @SerialName("status_code")
    val statusCode: Int
)