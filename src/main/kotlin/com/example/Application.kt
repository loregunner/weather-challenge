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
    val redisUrl = System.getenv("rediss://:pdf7aab6b655af439a0ba76b43e75cb39a7866e3183993f2b64d3e77c97f29a81@ec2-3-210-6-135.compute-1.amazonaws.com:19290") ?: "redis://localhost:6379"
    val redisClient = Jedis(redisUrl)

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/weather/{city}") {
                val city = call.parameters["city"]
                if (city != null) {
                    val cachedWeather = redisClient.get("weather:$city")
                    if (cachedWeather != null) {
                        call.respond(HttpStatusCode.OK, mapOf("city" to city, "temperature" to cachedWeather))
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Clima no encontrado para la ciudad: $city")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Por favor proporciona una ciudad")
                }
            }
        }
    }.start(wait = true)
}
