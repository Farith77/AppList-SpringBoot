package com.dreamapps.AppList.controller

import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.service.ListaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listas")
class ListaController(private val listaService: ListaService) {

    // 1. Obtener todas las listas (Visualización inicial)
    @GetMapping
    fun obtenerListas(): ResponseEntity<List<Lista>> {
        val listas = listaService.obtenerTodasLasListas()
        return ResponseEntity.ok(listas)
    }

    // 2. Crear una nueva lista (Estado de edición/creación)
    @PostMapping
    fun crearLista(@RequestBody nuevaLista: Lista): ResponseEntity<Lista> {
        val listaCreada = listaService.crearLista(nuevaLista)
        return ResponseEntity.status(HttpStatus.CREATED).body(listaCreada)
    }

    // 3. Eliminar una lista (Requisito Edu-01)
    @DeleteMapping("/{id}")
    fun eliminarLista(@PathVariable id: Int): ResponseEntity<Void> {
        listaService.eliminarLista(id)
        return ResponseEntity.noContent().build()
    }
}