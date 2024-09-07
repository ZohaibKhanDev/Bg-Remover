package com.example.bgremover.domain.model.imageenhance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnhanceResponse(
    @SerialName("data")
    val `data`: Data,
    @SerialName("status")
    val status: Int
)