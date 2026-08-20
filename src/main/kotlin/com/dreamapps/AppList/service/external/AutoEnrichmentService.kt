package com.dreamapps.AppList.service.external

import com.dreamapps.AppList.dto.ItemDetailDto
import com.dreamapps.AppList.service.external.client.AniListClient
import com.dreamapps.AppList.service.external.client.GoogleBooksClient
import com.dreamapps.AppList.service.external.client.TmdbClient
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeParseException
import com.dreamapps.AppList.service.external.client.RawgClient

@Service
class AutoEnrichmentService(
    private val tmdbClient: TmdbClient,
    private val googleBooksClient: GoogleBooksClient,
    private val aniListClient: AniListClient,
    private val rawgClient: RawgClient
) {
    fun buscarDetallesAutomaticos(nombreItem: String, formato: String?): ItemDetailDto? {
        val formatoLimpio = formato?.lowercase()

        return when (formatoLimpio) {
            "pelicula", "película", "saga de peliculas", "saga de películas" -> buscarPeliculaTmdb(nombreItem)

            // Series y Animes van a TMDB
            "serie", "anime" -> buscarSerieTmdb(nombreItem, formato)

            // Mangas y Novelas Ligeras van a AniList
            "manga", "novela" -> buscarMangaAniList(nombreItem, formato)

            // Libros tradicionales y Cómics occidentales van a Google Books
            "libro", "comic" -> buscarLibroGoogle(nombreItem, formato)

            // ¡Actualizamos para incluir Sagas!
            "videojuego", "juego", "game", "saga de videojuegos" -> buscarVideojuegoRawg(nombreItem)

            else -> null
        }
    }

    // --- PELÍCULAS Y SAGAS (TMDB) ---
    private fun buscarPeliculaTmdb(nombre: String): ItemDetailDto? {
        // 1. Intentamos buscar si es una Saga / Colección primero
        val respuestaColeccion = tmdbClient.buscarColeccion(nombre)
        val mejorColeccion = respuestaColeccion?.results?.firstOrNull()

        if (mejorColeccion != null) {
            val detalles = tmdbClient.obtenerDetallesColeccion(mejorColeccion.id)
            val urlImagen = mejorColeccion.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

            // ====================================================================
            // NUEVO: BUSCADOR DE PRÓXIMAS FECHAS EN LA SAGA
            // ====================================================================
            var esProximo = false
            var fechaProxima: LocalDate? = null

            detalles?.parts?.forEach { peliculaDeLaSaga ->
                if (!peliculaDeLaSaga.releaseDate.isNullOrBlank()) {
                    try {
                        val fechaEstreno = LocalDate.parse(peliculaDeLaSaga.releaseDate)
                        if (fechaEstreno.isAfter(LocalDate.now())) {
                            esProximo = true

                            // Guardamos la fecha si es la primera que encontramos en el futuro,
                            // o si es MÁS CERCANA que la que ya teníamos guardada.
                            if (fechaProxima == null || fechaEstreno.isBefore(fechaProxima)) {
                                fechaProxima = fechaEstreno
                            }
                        }
                    } catch (_: DateTimeParseException) { }
                }
            }

            return ItemDetailDto(
                itemCod = "",
                formatoItem = "Saga de Películas",
                cantidadEntregas = detalles?.parts?.size,
                proximoContenido = esProximo,
                fechaProximoContenido = fechaProxima,
                imagen = urlImagen,
                rating = null
            )
        }

        // 2. Si no encontró colección, hacemos la búsqueda normal de película individual
        val respuestaPelicula = tmdbClient.buscarPelicula(nombre)
        val mejorPeli = respuestaPelicula?.results?.firstOrNull() ?: return null

        val urlImagen = mejorPeli.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
        var esProximo = false
        var fechaProxima: LocalDate? = null

        if (!mejorPeli.releaseDate.isNullOrBlank()) {
            try {
                val fechaEstreno = LocalDate.parse(mejorPeli.releaseDate)
                if (fechaEstreno.isAfter(LocalDate.now())) {
                    esProximo = true
                    fechaProxima = fechaEstreno
                }
            } catch (_: DateTimeParseException) { }
        }

        return ItemDetailDto(
            itemCod = "",
            formatoItem = "Pelicula",
            cantidadEntregas = 1,
            proximoContenido = esProximo,
            fechaProximoContenido = fechaProxima,
            imagen = urlImagen,
            rating = null
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

    // --- VIDEOJUEGOS Y SAGAS (RAWG) ---
    private fun buscarVideojuegoRawg(nombre: String): ItemDetailDto? {
        val respuesta = rawgClient.buscarVideojuego(nombre)
        val mejor = respuesta?.results?.firstOrNull() ?: return null

        val respuestaSaga = rawgClient.obtenerSagaDeVideojuego(mejor.id)
        val otrosJuegos = respuestaSaga?.results ?: emptyList()
        val otrosJuegosEnSaga = respuestaSaga?.count ?: 0

        val formatoFinal = if (otrosJuegosEnSaga > 0) "Saga de Videojuegos" else "Videojuego"
        val totalEntregas = if (otrosJuegosEnSaga > 0) otrosJuegosEnSaga + 1 else 1

        var esProximo = mejor.tba
        var fechaProxima: LocalDate? = null

        // 1. Revisamos la fecha del juego principal (ej. GTA V)
        if (!mejor.released.isNullOrBlank()) {
            try {
                val fechaEstreno = LocalDate.parse(mejor.released)
                if (fechaEstreno.isAfter(LocalDate.now())) {
                    esProximo = true
                    fechaProxima = fechaEstreno
                }
            } catch (_: DateTimeParseException) { }
        }

        // 2. Revisamos las fechas de TODOS los demás juegos de la saga (ej. GTA VI)
        otrosJuegos.forEach { juegoSaga ->
            if (juegoSaga.tba) {
                esProximo = true // Si alguno está "Por anunciar", activamos el switch
            }

            if (!juegoSaga.released.isNullOrBlank()) {
                try {
                    val fechaEstreno = LocalDate.parse(juegoSaga.released)
                    if (fechaEstreno.isAfter(LocalDate.now())) {
                        esProximo = true
                        // Guardamos la fecha si es la primera futura que encontramos o la más cercana
                        if (fechaProxima == null || fechaEstreno.isBefore(fechaProxima)) {
                            fechaProxima = fechaEstreno
                        }
                    }
                } catch (_: DateTimeParseException) { }
            }
        }

        return ItemDetailDto(
            itemCod = "",
            formatoItem = formatoFinal,
            cantidadEntregas = totalEntregas,
            proximoContenido = esProximo,
            fechaProximoContenido = fechaProxima, // ¡Ahora sí atrapará el futuro!
            imagen = mejor.backgroundImage,
            rating = null
        )
    }
}