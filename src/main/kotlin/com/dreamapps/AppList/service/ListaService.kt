package com.dreamapps.AppList.service

import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.repository.ListaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListaService(private val listaRepository: ListaRepository) {

    // 1. Obtener solo listas activas del usuario
    fun obtenerTodasLasListas(user: Usuario): List<Lista> {
        return listaRepository.findByUserAndListActiveTrueOrderByListOrderAsc(user)
    }

    // 2. Crear Lista asignándole el usuario autenticado
    fun crearLista(user: Usuario, nuevaLista: Lista): Lista {
        if (nuevaLista.listName.isBlank()) {
            throw IllegalArgumentException("El nombre de la lista no puede estar vacío")
        }
        nuevaLista.user = user
        nuevaLista.listActive = true
        return listaRepository.save(nuevaLista)
    }

    // 3. Actualizar Lista validando pertenencia al usuario
    fun actualizarLista(user: Usuario, id: String, listaActualizada: Lista): Lista {
        val listaExistente = listaRepository.findByListCodAndUser(id, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $id") }

        listaExistente.listName = listaActualizada.listName
        listaExistente.listDescription = listaActualizada.listDescription
        listaExistente.listImage = listaActualizada.listImage
        listaExistente.listOrder = listaActualizada.listOrder
        listaExistente.listActive = listaActualizada.listActive

        return listaRepository.save(listaExistente)
    }

    // 4. Mover a papelera (Soft Delete) validando usuario
    fun moverAPapelera(user: Usuario, id: String) {
        val listaExistente = listaRepository.findByListCodAndUser(id, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $id") }

        listaExistente.listActive = false
        listaRepository.save(listaExistente)
    }

    // 5. Restaurar de la papelera validando usuario
    fun restaurarLista(user: Usuario, id: String) {
        val listaExistente = listaRepository.findByListCodAndUser(id, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $id") }

        listaExistente.listActive = true
        listaRepository.save(listaExistente)
    }

    // 6. Borrado físico (Hard Delete) validando usuario
    @Transactional
    fun eliminarListaFisicamente(user: Usuario, id: String) {
        val lista = listaRepository.findByListCodAndUser(id, user)
            .orElseThrow { IllegalArgumentException("La lista con ID $id no existe o no pertenece al usuario") }

        listaRepository.delete(lista)
    }

    // 7. Vaciar únicamente la papelera del usuario autenticado
    @Transactional
    fun vaciarPapelera(user: Usuario) {
        val listasEnPapelera = listaRepository.findByUserAndListActiveFalseOrderByListOrderAsc(user)
        if (listasEnPapelera.isNotEmpty()) {
            listaRepository.deleteAll(listasEnPapelera)
        }
    }

    // 8. Obtener historial completo (activas e inactivas) del usuario
    fun obtenerHistorialCompleto(user: Usuario): List<Lista> {
        return listaRepository.findByUserOrderByListOrderAsc(user)
    }
}