package com.dreamapps.AppList.controller

import com.dreamapps.AppList.dto.ItemDetailDto
import com.dreamapps.AppList.entity.ItemDetail
import com.dreamapps.AppList.service.ItemDetailService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listas/{listCod}/items/{itemCod}/detail")
class ItemDetailController(
    private val itemDetailService: ItemDetailService
) {

    // GET /api/listas/{listCod}/items/{itemCod}/detail
    @GetMapping
    fun obtenerDetalle(
        @PathVariable listCod: String,
        @PathVariable itemCod: String
    ): ResponseEntity<ItemDetail> {
        val detalle = itemDetailService.obtenerDetalle(itemCod)
        return ResponseEntity.ok(detalle)
    }

    // POST /api/listas/{listCod}/items/{itemCod}/detail
    @PostMapping
    fun guardarOActualizarDetalle(
        @PathVariable listCod: String,
        @PathVariable itemCod: String,
        @RequestBody dto: ItemDetailDto
    ): ResponseEntity<ItemDetail> {
        val dtoFinal = dto.copy(itemCod = itemCod)
        val detalleGuardado = itemDetailService.guardarOActualizarDetalle(dtoFinal)
        return ResponseEntity.ok(detalleGuardado)
    }

    // DELETE /api/listas/{listCod}/items/{itemCod}/detail
    @DeleteMapping
    fun eliminarDetalle(
        @PathVariable listCod: String,
        @PathVariable itemCod: String
    ): ResponseEntity<Map<String, String>> {
        itemDetailService.eliminarDetalle(itemCod)
        return ResponseEntity.ok(mapOf("message" to "Detalle eliminado correctamente"))
    }

    // POST /api/listas/{listCod}/items/{itemCod}/detail/auto-enrich?formato=Pelicula
    @PostMapping("/auto-enrich")
    fun autocompletarDetalle(
        @PathVariable listCod: String,
        @PathVariable itemCod: String,
        @RequestParam(required = false) formato: String?
    ): ResponseEntity<ItemDetail> {
        val detalleAutocompletado = itemDetailService.autocompletarDetalle(itemCod, formato)
        return ResponseEntity.ok(detalleAutocompletado)
    }
}