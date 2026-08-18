package com.dreamapps.AppList.service.external

import com.dreamapps.AppList.dto.ItemDetailDto
import com.dreamapps.AppList.service.external.client.AniListClient
import com.dreamapps.AppList.service.external.client.GoogleBooksClient
import com.dreamapps.AppList.service.external.client.TmdbClient
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Service
class AutoEnrichmentService(
    private val tmdbClient: TmdbClient,
    private val googleBooksClient: GoogleBooksClient,
    private val aniListClient: AniListClient
) {
    fun buscarDetallesAutomaticos(nombreItem: String, formato: String?): ItemDetailDto? {
        val formatoLimpio = formato?.lowercase()

        return when (formatoLimpio) {
            "pelicula" -> buscarPeliculaTmdb(nombreItem)

            // Series y Animes van a TMDB
            "serie", "anime" -> buscarSerieTmdb(nombreItem, formato)

            // Mangas y Novelas Ligeras van a AniList
            "manga", "novela" -> buscarMangaAniList(nombreItem, formato)

            // Libros tradicionales y Cómics occidentales van a Google Books
            "libro", "comic" -> buscarLibroGoogle(nombreItem, formato)

            else -> null
        }
    }

    // --- PELÍCULAS (TMDB) ---
    private fun buscarPeliculaTmdb(nombre: String): ItemDetailDto? {
        val respuesta = tmdbClient.buscarPelicula(nombre)
        val mejor = respuesta?.results?.firstOrNull() ?: return null

        val urlImagen = mejor.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
        var esProximo = false
        var fechaProxima: LocalDate? = null

        if (!mejor.releaseDate.isNullOrBlank()) {
            try {
                val fechaEstreno = LocalDate.parse(mejor.releaseDate)
                if (fechaEstreno.isAfter(LocalDate.now())) {
                    esProximo = true
                    fechaProxima = fechaEstreno
                }
            } catch (_: DateTimeParseException) { }
        }

        return ItemDetailDto(
            itemCod = "", formatoItem = "Pelicula", cantidadEntregas = null,
            proximoContenido = esProximo, fechaProximoContenido = fechaProxima,
            imagen = urlImagen, rating = null
        )
    }

    // --- SERIES Y ANIMES (TMDB) ---
    private fun buscarSerieTmdb(nombre: String, formatoOriginal: String?): ItemDetailDto? {
        val respuesta = tmdbClient.buscarSerie(nombre)
        val mejor = respuesta?.results?.firstOrNull() ?: return null
        val detalles = tmdbClient.obtenerDetallesSerie(mejor.id)

        val urlImagen = mejor.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
        val habraMas = detalles?.status == "Returning Series" || detalles?.status == "In Production"
        var fechaProxima: LocalDate? = null

        if (detalles?.nextEpisodeToAir?.airDate != null) {
            try { fechaProxima = LocalDate.parse(detalles.nextEpisodeToAir.airDate) }
            catch (_: DateTimeParseException) { }
        }

        val nombreFormato = formatoOriginal?.replaceFirstChar { it.uppercase() } ?: "Serie"

        return ItemDetailDto(
            itemCod = "", formatoItem = nombreFormato, cantidadEntregas = detalles?.numberOfSeasons,
            proximoContenido = habraMas || fechaProxima != null, fechaProximoContenido = fechaProxima,
            imagen = urlImagen, rating = null
        )
    }

    // --- MANGAS Y NOVELAS (AniList) ---
    private fun buscarMangaAniList(nombre: String, formatoOriginal: String?): ItemDetailDto? {
        val respuesta = aniListClient.buscarMangaONovela(nombre)
        val media = respuesta?.data?.media ?: return null

        // En mangas y novelas, tomamos los volúmenes. Si no hay volúmenes, tomamos los capítulos.
        val entregas = media.volumes ?: media.chapters

        // Verificamos si sigue en publicación
        val sigueEnPublicacion = media.status == "RELEASING" || media.status == "NOT_YET_RELEASED" || media.status == "HIATUS"

        return ItemDetailDto(
            itemCod = "",
            formatoItem = formatoOriginal?.replaceFirstChar { it.uppercase() } ?: "Manga",
            cantidadEntregas = entregas,
            proximoContenido = sigueEnPublicacion,
            fechaProximoContenido = null, // Los mangas no suelen dar fecha exacta del próximo capítulo en la API
            imagen = media.coverImage?.extraLarge,
            rating = null
        )
    }

    // --- LIBROS Y CÓMICS (Google Books) ---
    private fun buscarLibroGoogle(nombre: String, formatoOriginal: String?): ItemDetailDto? {
        val respuesta = googleBooksClient.buscarLibro(nombre)
        val mejor = respuesta?.items?.firstOrNull()?.volumeInfo ?: return null

        val urlImagen = mejor.imageLinks?.thumbnail?.replace("http:", "https:")

        return ItemDetailDto(
            itemCod = "",
            formatoItem = formatoOriginal?.replaceFirstChar { it.uppercase() } ?: "Libro",
            cantidadEntregas = null,
            proximoContenido = false,
            fechaProximoContenido = null,
            imagen = urlImagen,
            rating = null
        )
    }
}