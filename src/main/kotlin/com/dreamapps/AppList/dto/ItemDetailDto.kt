package com.dreamapps.AppList.dto

import java.time.LocalDate

data class ItemDetailDto(
    val itemCod: String,
    val formatoItem: String? = null,
    val cantidadEntregas: Int? = null,
    val proximoContenido: Boolean? = false,
    val fechaProximoContenido: LocalDate? = null,
    val imagen: String? = null,
    val rating: Int? = null
)