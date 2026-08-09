package com.dreamapps.AppList.service

import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.repository.ListaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListaService(private val listaRepository: ListaRepository) {

    // 1. Obtener solo listas activas (Ignoramos la papelera)
    fun obtenerTodasLasListas(): List<Lista> {
        return listaRepository.findByListActiveTrue()
    }

    // 2. Crear Lista
    fun crearLista(nuevaLista: Lista): Lista {
        if (nuevaLista.listName.isBlank()) {
            throw IllegalArgumentException("El nombre de la lista no puede estar vacío")
        }
        // Por si acaso, nos aseguramos de que nazca activa
        nuevaLista.listActive = true
        return listaRepository.save(nuevaLista)
    }

    // 3. Actualizar Lista
    fun actualizarLista(id: String, listaActualizada: Lista): Lista {
        val listaExistente = listaRepository.findById(id)
            .orElseThrow { Exception("Lista no encontrada con el id: $id") }

        listaExistente.listName = listaActualizada.listName
        listaExistente.listDescription = listaActualizada.listDescription
        listaExistente.listImage = listaActualizada.listImage
        listaExistente.listOrder = listaActualizada.listOrder
        listaExistente.listActive = listaActualizada.listActive

        return listaRepository.save(listaExistente)
    }

    // 4. Mover a papelera (Soft Delete)
    fun moverAPapelera(id: String) {
        val listaExistente = listaRepository.findById(id)
            .orElseThrow { Exception("Lista no encontrada con el id: $id") }

        listaExistente.listActive = false // La marcamos como eliminada
        listaRepository.save(listaExistente)
    }

    // 5. Restaurar de la papelera
    fun restaurarLista(id: String) {
        val listaExistente = listaRepository.findById(id)
            .orElseThrow { Exception("Lista no encontrada con el id: $id") }

        listaExistente.listActive = true // La volvemos a activar
        listaRepository.save(listaExistente)
    }

    // 6. Borrado físico (Hard Delete) de una sola lista
    @Transactional
    fun eliminarListaFisicamente(id: String) {
        // En lugar de deleteById directo, la buscamos para que Hibernate
        // cargue la relación y aplique la cascada correctamente.
        val lista = listaRepository.findById(id)
            .orElseThrow { IllegalArgumentException("La lista con ID $id no existe") }

        listaRepository.delete(lista)
    }

    // 7. Vaciar toda la papelera
    @Transactional
    fun vaciarPapelera() {
        // 1. Buscamos todas las listas que están en la papelera
        val listasEnPapelera = listaRepository.findByListActiveFalse()

        // 2. Las borramos pasándole la colección completa.
        // Esto fuerza a Hibernate a borrar los ítems de cada lista primero.
        if (listasEnPapelera.isNotEmpty()) {
            listaRepository.deleteAll(listasEnPapelera)
        }
    }

    // 8. Obtener ABSOLUTAMENTE TODAS las listas (activas e inactivas)
    fun obtenerHistorialCompleto(): List<Lista> {
        return listaRepository.findAll()
    }
}