package com.example.bgremover.domain.model.imageenhance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("completed_at")
    val completedAt: Int,
    @SerialName("created_at")
    val createdAt: Int,
    @SerialName("download_time")
    val downloadTime: Int,
    @SerialName("image")
    val image: String,
    @SerialName("image_height")
    val imageHeight: Int,
    @SerialName("image_width")
    val imageWidth: Int,
    @SerialName("processed_at")
    val processedAt: Int,
    @SerialName("progress")
    val progress: Int,
    @SerialName("return_type")
    val returnType: Int,
    @SerialName("state")
    val state: Int,
    @SerialName("state_detail")
    val stateDetail: String,
    @SerialName("task_id")
    val taskId: String,
    @SerialName("time_elapsed")
    val timeElapsed: Double,
    @SerialName("type")
    val type: String
)