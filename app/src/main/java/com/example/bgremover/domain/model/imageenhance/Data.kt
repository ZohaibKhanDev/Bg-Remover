package com.example.bgremover.domain.model.imageenhance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("image_url")
    val imageUrl: String
)