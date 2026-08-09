package com.dreamapps.AppList.controller

import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.service.ListaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listas")
class ListaController(private val listaService: ListaService) {

    // 1. Obtener todas las listas activas (Visualización inicial)
    @GetMapping
    fun obtenerListas(): ResponseEntity<List<Lista>> {
        val listas = listaService.obtenerTodasLasListas()
        return ResponseEntity.ok(listas)
    }

    // 2. Crear una nueva lista con el UUID generado en Android
    @PostMapping
    fun crearLista(@RequestBody nuevaLista: Lista): ResponseEntity<Lista> {
        val listaCreada = listaService.crearLista(nuevaLista)
        return ResponseEntity.status(HttpStatus.CREATED).body(listaCreada)
    }

    // 3. Modificar una lista (Ej. cambiar el título)
    @PutMapping("/{id}")
    fun actualizarLista(
        @PathVariable id: String,
        @RequestBody listaActualizada: Lista
    ): ResponseEntity<Lista> {
        return try {
            val lista = listaService.actualizarLista(id, listaActualizada)
            ResponseEntity.ok(lista)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    // 4. Mover a papelera (Soft Delete)
    @DeleteMapping("/{id}")
    fun moverAPapelera(@PathVariable id: String): ResponseEntity<Void> {
        listaService.moverAPapelera(id)
        return ResponseEntity.noContent().build()
    }

    // 5. Restaurar de la papelera
    @PutMapping("/{id}/restaurar")
    fun restaurarLista(@PathVariable id: String): ResponseEntity<Void> {
        listaService.restaurarLista(id)
        return ResponseEntity.ok().build()
    }

    // 6. Eliminar físicamente (Hard Delete de una sola lista)
    @DeleteMapping("/{id}/fisica")
    fun eliminarListaFisicamente(@PathVariable id: String): ResponseEntity<Void> {
        listaService.eliminarListaFisicamente(id)
        return ResponseEntity.noContent().build()
    }

    // 7. Vaciar toda la papelera
    @DeleteMapping("/papelera")
    fun vaciarPapelera(): ResponseEntity<Void> {
        listaService.vaciarPapelera()
        return ResponseEntity.noContent().build()
    }

    // 8. Ruta para ver todas las listas mezcladas (activas y borradas)
    @GetMapping("/historial")
    fun obtenerHistorial(): ResponseEntity<List<Lista>> {
        val listas = listaService.obtenerHistorialCompleto()
        return ResponseEntity.ok(listas)
    }
}