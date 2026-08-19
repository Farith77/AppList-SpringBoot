package com.dreamapps.AppList.service.external.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class TmdbClient(
    @Value("\${tmdb.api.url}") private val baseUrl: String,
    @Value("\${tmdb.api.token}") private val token: String
) {
    // RestClient es la herramienta moderna de Spring Boot para consumir APIs
    private val restClient = RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Authorization", "Bearer $token") // Inyectamos tu token seguro
        .defaultHeader("Accept", "application/json")
        .build()

    fun probarConexion(): String {
        // Hacemos una consulta rápida de prueba a TMDB
        return restClient.get()
            .uri("/authentication") // Este endpoint verifica si tu token es válido
            .retrieve()
            .body(String::class.java) ?: "Sin respuesta"
    }

    // Buscar película por nombre
    fun buscarPelicula(query: String): TmdbSearchResponse? {
        return restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/search/movie")
                    .queryParam("query", query)
                    .queryParam("language", "es-MX") // Pedimos los datos en español
                    .build()
            }
            .retrieve()
            .body(TmdbSearchResponse::class.java)
    }

    // Buscar serie por nombre (Paso 1)
    fun buscarSerie(query: String): TmdbTvSearchResponse? {
        return restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/search/tv")
                    .queryParam("query", query)
                    .queryParam("language", "es-MX")
                    .build()
            }
            .retrieve()
            .body(TmdbTvSearchResponse::class.java)
    }

    // Obtener detalles extras como las temporadas (Paso 2)
    fun obtenerDetallesSerie(tvId: Int): TmdbTvDetails? {
        return restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/tv/$tvId")
                    .queryParam("language", "es-MX")
                    .build()
            }
            .retrieve()
            .body(TmdbTvDetails::class.java)
    }

    // Buscar colección/saga por nombre
    fun buscarColeccion(query: String): TmdbCollectionSearchResponse? {
        return restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/search/collection")
                    .queryParam("query", query)
                    .queryParam("language", "es-MX") // Mantenemos el idioma en español
                    .build()
            }
            .retrieve()
            .body(TmdbCollectionSearchResponse::class.java)
    }

    // Obtener detalles de la colección (para contar cuántas películas la conforman)
    fun obtenerDetallesColeccion(collectionId: Int): TmdbCollectionDetailResponse? {
        return restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/collection/$collectionId")
                    .queryParam("language", "es-MX")
                    .build()
            }
            .retrieve()
            .body(TmdbCollectionDetailResponse::class.java)
    }
}