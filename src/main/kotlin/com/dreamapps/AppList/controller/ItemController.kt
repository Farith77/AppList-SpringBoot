package com.dreamapps.AppList.controller

import com.dreamapps.AppList.entity.Item
import com.dreamapps.AppList.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listas/{listCod}/items")
class ItemController(private val itemService: ItemService) {

    @GetMapping
    fun obtenerItems(@PathVariable listCod: String): ResponseEntity<List<Item>> {
        val items = itemService.obtenerItemsPorLista(listCod)
        return ResponseEntity.ok(items)
    }

    @PostMapping
    fun agregarItem(
        @PathVariable listCod: String,
        @RequestBody nuevoItem: Item
    ): ResponseEntity<Item> {
        val itemCreado = itemService.agregarItemALista(listCod, nuevoItem)
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCreado)
    }

    // NUEVO: Endpoint para modificar el texto de un ítem
    @PutMapping("/{itemCod}")
    fun actualizarItem(
        @PathVariable listCod: String,
        @PathVariable itemCod: String,
        @RequestBody itemActualizado: Item
    ): ResponseEntity<Item> {
        return try {
            val itemModificado = itemService.actualizarItem(listCod, itemCod, itemActualizado)
            ResponseEntity.ok(itemModificado)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{itemCod}")
    fun eliminarItem(
        @PathVariable listCod: String,
        @PathVariable itemCod: String
    ): ResponseEntity<Void> {
        itemService.eliminarItem(itemCod)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/orden")
    fun actualizarOrden(
        @PathVariable listCod: String,
        @RequestBody items: List<Item>
    ): ResponseEntity<Void> {
        itemService.actualizarOrdenItems(listCod, items)
        return ResponseEntity.ok().build()
    }
}