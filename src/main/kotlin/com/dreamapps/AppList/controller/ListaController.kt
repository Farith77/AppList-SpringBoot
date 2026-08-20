package com.dreamapps.AppList.controller

import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.service.ListaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listas")
class ListaController(private val listaService: ListaService) {

    // 1. Obtener todas las listas activas del usuario
    @GetMapping
    fun obtenerListas(@AuthenticationPrincipal user: Usuario): ResponseEntity<List<Lista>> {
        val listas = listaService.obtenerTodasLasListas(user)
        return ResponseEntity.ok(listas)
    }

    // 2. Crear una nueva lista asociada al usuario
    @PostMapping
    fun crearLista(
        @AuthenticationPrincipal user: Usuario,
        @RequestBody nuevaLista: Lista
    ): ResponseEntity<Lista> {
        val listaCreada = listaService.crearLista(user, nuevaLista)
        return ResponseEntity.status(HttpStatus.CREATED).body(listaCreada)
    }

    // 3. Modificar una lista del usuario
    @PutMapping("/{id}")
    fun actualizarLista(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable id: String,
        @RequestBody listaActualizada: Lista
    ): ResponseEntity<Lista> {
        return try {
            val lista = listaService.actualizarLista(user, id, listaActualizada)
            ResponseEntity.ok(lista)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    // 4. Mover a papelera (Soft Delete)
    @DeleteMapping("/{id}")
    fun moverAPapelera(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable id: String
    ): ResponseEntity<Void> {
        listaService.moverAPapelera(user, id)
        return ResponseEntity.noContent().build()
    }

    // 5. Restaurar de la papelera
    @PutMapping("/{id}/restaurar")
    fun restaurarLista(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable id: String
    ): ResponseEntity<Void> {
        listaService.restaurarLista(user, id)
        return ResponseEntity.ok().build()
    }

    // 6. Eliminar físicamente (Hard Delete de una sola lista)
    @DeleteMapping("/{id}/fisica")
    fun eliminarListaFisicamente(
        @AuthenticationPrincipal user: Usuario,
        @PathVariable id: String
    ): ResponseEntity<Void> {
        listaService.eliminarListaFisicamente(user, id)
        return ResponseEntity.noContent().build()
    }

    // 7. Vaciar únicamente la papelera del usuario
    @DeleteMapping("/papelera")
    fun vaciarPapelera(@AuthenticationPrincipal user: Usuario): ResponseEntity<Void> {
        listaService.vaciarPapelera(user)
        return ResponseEntity.noContent().build()
    }

    // 8. Ruta para ver todas las listas (activas y borradas) del usuario
    @GetMapping("/historial")
    fun obtenerHistorial(@AuthenticationPrincipal user: Usuario): ResponseEntity<List<Lista>> {
        val listas = listaService.obtenerHistorialCompleto(user)
        return ResponseEntity.ok(listas)
    }
}