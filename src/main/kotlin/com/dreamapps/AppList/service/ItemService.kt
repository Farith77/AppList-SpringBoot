package com.dreamapps.AppList.service

import com.dreamapps.AppList.entity.Item
import com.dreamapps.AppList.repository.ItemRepository
import com.dreamapps.AppList.repository.ListaRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val listaRepository: ListaRepository
) {

    fun obtenerItemsPorLista(listCod: Int): List<Item> {
        return itemRepository.findByListaListCodOrderByItemOrderAsc(listCod)
    }

    fun agregarItemALista(listCod: Int, nuevoItem: Item): Item {
        // Validamos que el nombre no esté vacío según tus estándares SQA
        if (nuevoItem.itemName.isBlank()) {
            throw IllegalArgumentException("El ítem no puede estar vacío")
        }

        // Buscamos la lista. Si no existe, lanzamos un error.
        val listaAsociada = listaRepository.findById(listCod)
            .orElseThrow { IllegalArgumentException("La lista con ID $listCod no existe") }

        // Enlazamos el ítem a la lista y guardamos
        nuevoItem.lista = listaAsociada
        return itemRepository.save(nuevoItem)
    }

    fun eliminarItem(itemCod: Int) {
        itemRepository.deleteById(itemCod)
    }
}