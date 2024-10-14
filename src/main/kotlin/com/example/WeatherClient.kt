package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class WeatherClient(private val apiKey: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getWeather(city: String, lat: Double, lon: Double, retries: Int = 3): Double {
        repeat(retries) { attempt ->
            try {
                val response = client.get("https://api.tomorrow.io/v4/timelines") {
                    parameter("location", "$lat,$lon")
                    parameter("fields", "temperature")
                    parameter("timesteps", "current")
                    parameter("units", "metric")
                    parameter("apikey", apiKey)
                }

                val result: WeatherResponse = response.body()
                return result.data.timelines[0].intervals[0].values.temperature
            } catch (e: Exception) {
                println("Intento ${attempt + 1} fallido para $city: ${e.message}")
                if (attempt == retries - 1) {
                    throw e
                }
                delay(5000)
            }
        }
        throw RuntimeException("No se pudo obtener el clima para $city después de $retries intentos.")
    }

    suspend fun close() {
        client.close()
    }
}

@Serializable
data class WeatherResponse(val data: WeatherData)

@Serializable
data class WeatherData(val timelines: List<Timeline>)

@Serializable
data class Timeline(val intervals: List<Interval>)

@Serializable
data class Interval(val values: WeatherValues)

@Serializable
data class WeatherValues(val temperature: Double)
