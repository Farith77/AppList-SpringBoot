package com.dreamapps.AppList.service.external.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

// Esta clase atrapa la lista de resultados que devuelve TMDB
@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbSearchResponse(
    val results: List<TmdbMovie>
)

// Esta clase atrapa los datos específicos de cada película
@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbMovie(
    val id: Int,
    val title: String,

    @JsonProperty("release_date")
    val releaseDate: String?, // TMDB lo manda como "2002-05-01"

    @JsonProperty("poster_path")
    val posterPath: String?, // TMDB lo manda como "/algo.jpg"

    @JsonProperty("vote_average")
    val voteAverage: Double? // TMDB lo manda como 7.3
)

// --- CLASES PARA SERIES ---

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbTvSearchResponse(
    val results: List<TmdbTv>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbTv(
    val id: Int,
    val name: String,
    @JsonProperty("poster_path")
    val posterPath: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbTvDetails(
    @JsonProperty("number_of_seasons")
    val numberOfSeasons: Int?,

    val status: String?, // "Returning Series", "Ended", etc.

    // NUEVO: Atrapamos el objeto del próximo episodio
    @JsonProperty("next_episode_to_air")
    val nextEpisodeToAir: TmdbEpisode?
)

// NUEVO: Clase para leer la fecha de ese episodio
@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbEpisode(
    @JsonProperty("air_date")
    val airDate: String?
)