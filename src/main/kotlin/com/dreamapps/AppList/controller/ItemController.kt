package com.dreamapps.AppList.controller

import com.dreamapps.AppList.entity.Item
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listas/{listCod}/items")
class ItemController(private val itemService: ItemService) {

    @GetMapping
    fun obtenerItems(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String
    ): ResponseEntity<List<Item>> {
        val items = itemService.obtenerItemsPorLista(user, listCod)
        return ResponseEntity.ok(items)
    }

    @PostMapping
    fun agregarItem(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @RequestBody nuevoItem: Item
    ): ResponseEntity<Item> {
        val itemCreado = itemService.agregarItemALista(user, listCod, nuevoItem)
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCreado)
    }

    // Endpoint para modificar el texto de un ítem
    @PutMapping("/{itemCod}")
    fun actualizarItem(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @PathVariable itemCod: String,
        @RequestBody itemActualizado: Item
    ): ResponseEntity<Item> {
        return try {
            val itemModificado = itemService.actualizarItem(user, listCod, itemCod, itemActualizado)
            ResponseEntity.ok(itemModificado)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{itemCod}")
    fun eliminarItem(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @PathVariable itemCod: String
    ): ResponseEntity<Void> {
        itemService.eliminarItem(user, listCod, itemCod)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/orden")
    fun actualizarOrden(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable listCod: String,
        @RequestBody items: List<Item>
    ): ResponseEntity<Void> {
        itemService.actualizarOrdenItems(user, listCod, items)
        return ResponseEntity.ok().build()
    }
}