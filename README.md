# Weather Challenge

## Descripción

Este proyecto es una aplicación que obtiene información sobre el clima de diferentes ciudades y almacena los resultados en Redis. Utiliza Ktor como framework para el servidor y Jedis para la conexión a Redis.

## Estructura del Proyecto

- `src/main/kotlin/com/example`: Contiene el código fuente de la aplicación.
- `WeatherClient.kt`: Maneja las solicitudes a la API de clima.
- `Application.kt`: Define la configuración del servidor Ktor y los endpoints.
- `Main.kt`: Punto de entrada de la aplicación que actualiza la información del clima en Redis.

## Requisitos

- JDK 11 o superior
- Gradle
- Redis

## Instalación

1. **Clona el repositorio**:
   ```bash
   git clone https://github.com/tuusuario/weather-challenge.git
   cd weather-challenge
   ```

2. **Instalar dependecias
 ```bash
./gradlew build
   ```
3. **Redis
 ```bash
redis-server
   ```
4. **Ejecutar la aplicación
 ```bash
./gradlew run
   ```
5. **Obtener el clima
   
GET http://localhost:8080/weather/{ciudad}
