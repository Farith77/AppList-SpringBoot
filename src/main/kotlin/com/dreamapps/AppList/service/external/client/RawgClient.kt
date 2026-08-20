package com.dreamapps.AppList.service.external.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RawgClient(
    @Value("\${rawg.api.url}") private val baseUrl: String,
    @Value("\${rawg.api.key}") private val apiKey: String
) {
    private val restClient = RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Accept", "application/json")
        .build()

    fun buscarVideojuego(query: String): RawgSearchResponse? {
        return restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/games")
                    .queryParam("key", apiKey) // RAWG exige la llave aquí
                    .queryParam("search", query)
                    .build()
            }
            .retrieve()
            .body(RawgSearchResponse::class.java)
    }

    fun obtenerSagaDeVideojuego(gameId: Int): RawgSeriesResponse? {
        return restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/games/$gameId/game-series")
                    .queryParam("key", apiKey)
                    .build()
            }
            .retrieve()
            .body(RawgSeriesResponse::class.java)
    }
}