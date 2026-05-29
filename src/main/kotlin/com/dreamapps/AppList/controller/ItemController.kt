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
    fun obtenerItems(@PathVariable listCod: Int): ResponseEntity<List<Item>> {
        val items = itemService.obtenerItemsPorLista(listCod)
        return ResponseEntity.ok(items)
    }

    @PostMapping
    fun agregarItem(
        @PathVariable listCod: Int,
        @RequestBody nuevoItem: Item
    ): ResponseEntity<Item> {
        val itemCreado = itemService.agregarItemALista(listCod, nuevoItem)
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCreado)
    }

    @DeleteMapping("/{itemCod}")
    fun eliminarItem(
        @PathVariable listCod: Int,
        @PathVariable itemCod: Int
    ): ResponseEntity<Void> {
        itemService.eliminarItem(itemCod)
        return ResponseEntity.noContent().build()
    }
}