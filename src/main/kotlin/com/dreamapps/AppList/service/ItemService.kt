package com.dreamapps.AppList.service

import com.dreamapps.AppList.entity.Item
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.repository.ItemRepository
import com.dreamapps.AppList.repository.ListaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val listaRepository: ListaRepository
) {

    fun obtenerItemsPorLista(user: Usuario, listCod: String): List<Item> {
        val lista = listaRepository.findByListCodAndUser(listCod, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $listCod") }

        return itemRepository.findByListaListCodOrderByItemOrderAsc(lista.listCod)
    }

    fun agregarItemALista(user: Usuario, listCod: String, nuevoItem: Item): Item {
        if (nuevoItem.itemName.isBlank()) {
            throw IllegalArgumentException("El ítem no puede estar vacío")
        }
        val listaAsociada = listaRepository.findByListCodAndUser(listCod, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $listCod") }

        nuevoItem.lista = listaAsociada
        return itemRepository.save(nuevoItem)
    }

    fun actualizarItem(user: Usuario, listCod: String, itemCod: String, itemActualizado: Item): Item {
        listaRepository.findByListCodAndUser(listCod, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $listCod") }

        val itemExistente = itemRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("Ítem no encontrado con ID: $itemCod") }

        if (itemExistente.lista?.listCod != listCod) {
            throw IllegalArgumentException("Este ítem no pertenece a la lista indicada")
        }

        if (itemActualizado.itemName.isNotBlank()) {
            itemExistente.itemName = itemActualizado.itemName
        }

        return itemRepository.save(itemExistente)
    }

    @Transactional
    fun eliminarItem(user: Usuario, listCod: String, itemCod: String) {
        listaRepository.findByListCodAndUser(listCod, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $listCod") }

        val itemExistente = itemRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("Ítem no encontrado con ID: $itemCod") }

        if (itemExistente.lista?.listCod != listCod) {
            throw IllegalArgumentException("Este ítem no pertenece a la lista indicada")
        }

        itemRepository.delete(itemExistente)
    }

    @Transactional
    fun actualizarOrdenItems(user: Usuario, listCod: String, itemsActualizados: List<Item>) {
        listaRepository.findByListCodAndUser(listCod, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $listCod") }

        itemsActualizados.forEach { itemRecibido ->
            val itemExistente = itemRepository.findById(itemRecibido.itemCod).orElse(null)
            if (itemExistente != null && itemExistente.lista?.listCod == listCod) {
                itemExistente.itemOrder = itemRecibido.itemOrder
                itemRepository.save(itemExistente)
            }
        }
    }
}