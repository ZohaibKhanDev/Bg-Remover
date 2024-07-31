package com.example.bgremover.data.remote

import com.example.bgremover.utils.constant.TIMEOUT
import io.ktor.client.HttpClient
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
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.io.File

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
            append("image", imageFile.readBytes(), Headers.build {
                append(HttpHeaders.ContentType, "image/png")
                append(HttpHeaders.ContentDisposition, "filename=\"${imageFile.name}\"")
            })
            append("return_form", "")
        }

        val response: HttpResponse =
            client.post("https://Human-Background-Removal.proxy-production.allthingsdev.co/cutout/portrait/body") {
                headers {
                    append("x-apihub-key", "fSu5mUcN3iMoxx0ZMJVRahfBjO8maLx4WXQnn6cXXp4w2999by")
                    append("x-apihub-host", "Human-Background-Removal.allthingsdev.co")
                    append("x-apihub-endpoint", "fde322f3-7402-43c6-87d1-23961c255735")
                }
                body = MultiPartFormDataContent(formData)
            }

        return response.bodyAsText()
    }

}