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

    fun obtenerItemsPorLista(listCod: String): List<Item> {
        return itemRepository.findByListaListCodOrderByItemOrderAsc(listCod)
    }

    fun agregarItemALista(listCod: String, nuevoItem: Item): Item {
        if (nuevoItem.itemName.isBlank()) {
            throw IllegalArgumentException("El ítem no puede estar vacío")
        }
        val listaAsociada = listaRepository.findById(listCod)
            .orElseThrow { IllegalArgumentException("La lista con ID $listCod no existe") }

        nuevoItem.lista = listaAsociada
        // Al recibir un UUID desde Android, Hibernate hará un SELECT silencioso,
        // verá que no existe y ejecutará un INSERT usando el UUID de Android. ¡Perfecto!
        return itemRepository.save(nuevoItem)
    }

    // NUEVO: Metodo para actualizar el texto de un ítem existente
    fun actualizarItem(listCod: String, itemCod: String, itemActualizado: Item): Item {
        val itemExistente = itemRepository.findById(itemCod)
            .orElseThrow { Exception("Ítem no encontrado con ID: $itemCod") }

        // Medida de seguridad: Validar que el ítem realmente pertenezca a la lista
        if (itemExistente.lista?.listCod != listCod) {
            throw IllegalArgumentException("Este ítem no pertenece a la lista indicada")
        }

        if (itemActualizado.itemName.isNotBlank()) {
            itemExistente.itemName = itemActualizado.itemName
        }

        return itemRepository.save(itemExistente)
    }

    fun eliminarItem(itemCod: String) {
        itemRepository.deleteById(itemCod)
    }

    fun actualizarOrdenItems(listCod: String, itemsActualizados: List<Item>) {
        itemsActualizados.forEach { itemRecibido ->
            val itemExistente = itemRepository.findById(itemRecibido.itemCod).orElse(null)
            if (itemExistente != null && itemExistente.lista?.listCod == listCod) {
                itemExistente.itemOrder = itemRecibido.itemOrder
                itemRepository.save(itemExistente)
            }
        }
    }
}