package com.example.bgremover.data.remote

import android.content.Context
import android.net.Uri
import com.example.bgremover.domain.model.imageenhance.EnhanceResponse
import com.example.bgremover.utils.constant.TIMEOUT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.post              
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI        
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single
import java.io.File
import java.io.InputStream


@Single
object BgRemoverApiClient {
    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    prettyPrint = true
                }
            )
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }

            }
        }
        
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT
            socketTimeoutMillis = TIMEOUT
            requestTimeoutMillis = TIMEOUT
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun removeBackground(imageFile: File): String {
        val formData = formData {
            append("image_file", imageFile.readBytes(), Headers.build {
                append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                append(HttpHeaders.ContentDisposition, "filename=\"${imageFile.name}\"")
            })
            append("size", "auto")
        }
        val response: HttpResponse = client.post("https://api.remove.bg/v1.0/removebg") {
            headers {
                append("X-API-Key", "Sk6KjRkjFobTsC8tATmhdHtU")
            }
            setBody(MultiPartFormDataContent(formData))
        }
        if (response.status.isSuccess()) {
            val responseBody = response.body<JsonObject>()
            return responseBody["data"]?.jsonObject?.get("result_b64")?.jsonPrimitive?.content
                ?: throw Exception("Failed to parse response")
        } else {
            throw Exception("Failed to remove background: ${response.status}")
        }
    }

    suspend fun enhanceImage(imageUrl: String): EnhanceResponse {
        val response: HttpResponse = client.post("https://techhk.aoscdn.com/api/tasks/visual/scale") {
            headers {
                append("X-API-KEY", "wxiaj6kdky4abi1mk")
            }
            setBody(MultiPartFormDataContent(
                formData {
                    append("sync", "1")
                    append("image_url", imageUrl)
                }
            ))
        }

        return if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            Json.decodeFromString<EnhanceResponse>(responseBody)
        } else {
            throw Exception("Failed to enhance image: ${response.status} - ${response.bodyAsText()}")
        }
    }


}
