package com.example

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import redis.clients.jedis.Jedis

fun main() {
    val redisUrl = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
    val redisClient = Jedis(redisUrl)

    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/weather/{city}") {
                val city = call.parameters["city"]
                if (city != null) {
                    try {
                        val cachedWeather = redisClient.get("weather:$city")
                        if (cachedWeather != null) {
                            call.respond(HttpStatusCode.OK, mapOf("city" to city, "temperature" to cachedWeather))
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Clima no encontrado para la ciudad: $city")
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Error al acceder a Redis: ${e.message}")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Por favor proporciona una ciudad")
                }
            }
        }

        environment.monitor.subscribe(ApplicationStopping) {
            redisClient.close()
        }
    }.start(wait = true)
}
