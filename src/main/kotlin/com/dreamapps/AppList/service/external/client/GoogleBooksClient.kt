package com.dreamapps.AppList.service.external.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class GoogleBooksClient(
    @Value("\${google.books.api.key}") private val apiKey: String // <-- Inyectamos tu llave
) {
    private val restClient = RestClient.create("https://www.googleapis.com/books/v1")

    fun buscarLibro(query: String): GoogleBooksResponse? {
        return try {
            restClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("/volumes")
                        .queryParam("q", query)
                        .queryParam("maxResults", 1)
                        .queryParam("key", apiKey) // <-- Añadimos la llave a la petición
                        .build()
                }
                .retrieve()
                .body(GoogleBooksResponse::class.java)
        } catch (e: Exception) {
            println("🔥 ERROR AL LLAMAR A GOOGLE BOOKS: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}