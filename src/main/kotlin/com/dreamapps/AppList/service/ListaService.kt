package com.dreamapps.AppList.service

import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.repository.ListaRepository
import org.springframework.stereotype.Service

@Service
class ListaService(private val listaRepository: ListaRepository) {

    // Método para obtener todas las listas
    fun obtenerTodasLasListas(): List<Lista> {
        return listaRepository.findAll()
    }

    // Método para crear una nueva lista
    fun crearLista(nuevaLista: Lista): Lista {
        // Aquí entra la Calidad de Software (SQA)
        // Ejemplo de regla de negocio: Si no envían orden, lo ponemos en 0 por defecto
        if (nuevaLista.listName.isBlank()) {
            throw IllegalArgumentException("El nombre de la lista no puede estar vacío")
        }

        return listaRepository.save(nuevaLista)
    }

    // Método para eliminar una lista (Requisito Edu-01)
    fun eliminarLista(id: Int) {
        listaRepository.deleteById(id)
    }
}