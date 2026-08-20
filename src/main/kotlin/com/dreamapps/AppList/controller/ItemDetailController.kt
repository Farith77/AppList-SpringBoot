package com.dreamapps.AppList.controller

import com.dreamapps.AppList.dto.ItemDetailDto
import com.dreamapps.AppList.entity.ItemDetail
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.service.ItemDetailService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listas/{listCod}/items/{itemCod}/detail")
class ItemDetailController(
    private val itemDetailService: ItemDetailService
) {

    // GET /api/listas/{listCod}/items/{itemCod}/detail
    @GetMapping
    fun obtenerDetalle(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @PathVariable itemCod: String
    ): ResponseEntity<ItemDetail> {
        val detalle = itemDetailService.obtenerDetalle(user, listCod, itemCod)
        return ResponseEntity.ok(detalle)
    }

    // POST o PUT /api/listas/{listCod}/items/{itemCod}/detail
    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT])
    fun guardarOActualizarDetalle(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @PathVariable itemCod: String,
        @RequestBody dto: ItemDetailDto
    ): ResponseEntity<ItemDetail> {
        val dtoFinal = dto.copy(itemCod = itemCod)
        val detalleGuardado = itemDetailService.guardarOActualizarDetalle(user, listCod, dtoFinal)
        return ResponseEntity.ok(detalleGuardado)
    }

    // DELETE /api/listas/{listCod}/items/{itemCod}/detail
    @DeleteMapping
    fun eliminarDetalle(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @PathVariable itemCod: String
    ): ResponseEntity<Map<String, String>> {
        itemDetailService.eliminarDetalle(user, listCod, itemCod)
        return ResponseEntity.ok(mapOf("message" to "Detalle eliminado correctamente"))
    }

    // POST o PUT /api/listas/{listCod}/items/{itemCod}/detail/auto-enrich?formato=Pelicula
    @RequestMapping(value = ["/auto-enrich"], method = [RequestMethod.POST, RequestMethod.PUT])
    fun autocompletarDetalle(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @PathVariable itemCod: String,
        @RequestParam(required = false) formato: String?
    ): ResponseEntity<ItemDetail> {
        val detalleAutocompletado = itemDetailService.autocompletarDetalle(user, listCod, itemCod, formato)
        return ResponseEntity.ok(detalleAutocompletado)
    }
}