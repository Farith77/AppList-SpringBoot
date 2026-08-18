package com.dreamapps.AppList.service.external.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class AniListClient {
    private val restClient = RestClient.create("https://graphql.anilist.co")

    fun buscarMangaONovela(query: String): AniListResponse? {
        // Le decimos a AniList que el tipo es MANGA (esto incluye Novelas Ligeras)
        val graphQLQuery = """
            query (${'$'}search: String) {
              Media (search: ${'$'}search, type: MANGA) {
                id
                format
                status
                chapters
                volumes
                coverImage { extraLarge }
              }
            }
        """.trimIndent()

        val requestBody = AniListGraphQLRequest(
            query = graphQLQuery,
            variables = mapOf("search" to query)
        )

        return try {
            restClient.post()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody)
                .retrieve()
                .body(AniListResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}