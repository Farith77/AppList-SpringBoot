package com.dreamapps.AppList.service.external.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleBooksResponse(
    val items: List<GoogleBookItem>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleBookItem(
    val volumeInfo: GoogleBookVolumeInfo?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleBookVolumeInfo(
    val title: String?,
    val pageCount: Int?,
    val imageLinks: GoogleBookImageLinks?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleBookImageLinks(
    val thumbnail: String?
)