package id.neotica.imageclassificationdemo.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

object KtorConfig {
    const val BASE_URL = "https://classification-api.dicoding.dev/"

    val httpClient = HttpClient {
        defaultRequest { url(BASE_URL) }


        install(Logging) {
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
            })
        }
    }
}