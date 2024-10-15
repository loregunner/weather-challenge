import com.example.WeatherClient
import kotlinx.coroutines.*
import redis.clients.jedis.Jedis
import java.net.URI

fun main() = runBlocking {
    val apiKey = "oBAQSIvmbcvln63OTKLQVF2qjVCbQ7ad"
    val weatherClient = WeatherClient(apiKey)

    val redisUrl = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
    val redisClient = Jedis(URI(redisUrl))

    val cities = mapOf(
        "Santiago" to Pair(-33.4489, -70.6693),
        "Zúrich" to Pair(47.3769, 8.5417),
        "Auckland" to Pair(-36.8485, 174.7633),
        "Sídney" to Pair(-33.8688, 151.2093),
        "Londres" to Pair(51.5074, -0.1278),
        "Georgia" to Pair(32.1656, -82.9001)
    )

    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
        while (isActive) {
            try {
                for ((city, coordinates) in cities) {
                    val temperature = weatherClient.getWeather(city, coordinates.first, coordinates.second)
                    redisClient.set("weather:$city", "$temperature")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(300000)
        }
    }

    awaitCancellation()

    Runtime.getRuntime().addShutdownHook(Thread {
        redisClient.close()
        scope.cancel()
    })
}
