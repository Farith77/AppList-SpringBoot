package com.dreamapps.AppList.service.external.client

import com.fasterxml.jackson.annotation.JsonProperty

data class RawgSearchResponse(
    val results: List<RawgGameResult>
)

data class RawgGameResult(
    val id: Int,
    val name: String,
    val released: String?, // RAWG lo devuelve en formato "YYYY-MM-DD"
    val tba: Boolean, // "To Be Announced" - Ideal para juegos en desarrollo
    @JsonProperty("background_image") val backgroundImage: String?
)

data class RawgSeriesResponse(
    val count: Int, // RAWG nos dirá cuántos juegos adicionales conforman esta saga
    val results: List<RawgGameResult>
)