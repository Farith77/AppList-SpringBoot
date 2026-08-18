package com.dreamapps.AppList.controller

import com.dreamapps.AppList.service.external.client.TmdbClient
import com.dreamapps.AppList.service.external.client.TmdbSearchResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestApiController(
    private val tmdbClient: TmdbClient
) {

    @GetMapping("/tmdb-auth")
    fun probarConexionTmdb(): ResponseEntity<String> {
        val respuesta = tmdbClient.probarConexion()
        return ResponseEntity.ok(respuesta)
    }

    @GetMapping("/tmdb-search")
    fun buscarPeliculaTest(@RequestParam query: String): ResponseEntity<TmdbSearchResponse> {
        val respuesta = tmdbClient.buscarPelicula(query)
        return if (respuesta != null) {
            ResponseEntity.ok(respuesta)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}