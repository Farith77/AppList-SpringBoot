package com.dreamapps.AppList.service.external.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class AniListGraphQLRequest(
    val query: String,
    val variables: Map<String, Any?>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListResponse(
    val data: AniListData?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListData(
    @JsonProperty("Media")
    val media: AniListMedia?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListMedia(
    val id: Int?,
    val format: String?, // Retorna "MANGA", "NOVEL", "ONE_SHOT"
    val status: String?, // "RELEASING", "FINISHED", "HIATUS", etc.
    val chapters: Int?,
    val volumes: Int?,
    val coverImage: AniListCoverImage?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListCoverImage(
    val extraLarge: String?
)