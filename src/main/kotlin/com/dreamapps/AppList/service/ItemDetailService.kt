package com.dreamapps.AppList.service

import com.dreamapps.AppList.dto.ItemDetailDto
import com.dreamapps.AppList.entity.ItemDetail
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.repository.ItemDetailRepository
import com.dreamapps.AppList.repository.ItemRepository
import com.dreamapps.AppList.repository.ListaRepository
import com.dreamapps.AppList.service.external.AutoEnrichmentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemDetailService(
    private val itemDetailRepository: ItemDetailRepository,
    private val itemRepository: ItemRepository,
    private val listaRepository: ListaRepository,
    private val autoEnrichmentService: AutoEnrichmentService
) {

    private fun validarPertenencia(user: Usuario, listCod: String, itemCod: String) {
        listaRepository.findByListCodAndUser(listCod, user)
            .orElseThrow { IllegalArgumentException("Lista no encontrada o no pertenece al usuario: $listCod") }

        val item = itemRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("El ítem con ID $itemCod no existe") }

        if (item.lista?.listCod != listCod) {
            throw IllegalArgumentException("Este ítem no pertenece a la lista indicada")
        }
    }

    // 1. Obtener detalles de un ítem
    fun obtenerDetalle(user: Usuario, listCod: String, itemCod: String): ItemDetail {
        validarPertenencia(user, listCod, itemCod)

        return itemDetailRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("No se encontraron detalles para el ítem con ID: $itemCod") }
    }

    // 2. Crear o Actualizar detalles manualmente
    @Transactional
    fun guardarOActualizarDetalle(user: Usuario, listCod: String, dto: ItemDetailDto): ItemDetail {
        val itemCod = dto.itemCod ?: throw IllegalArgumentException("El ID del ítem es requerido")
        validarPertenencia(user, listCod, itemCod)

        val item = itemRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("El ítem con ID $itemCod no existe") }

        val detalle = itemDetailRepository.findById(itemCod)
            .orElseGet { ItemDetail(item = item) }

        detalle.formatoItem = dto.formatoItem
        detalle.cantidadEntregas = dto.cantidadEntregas
        detalle.proximoContenido = dto.proximoContenido
        detalle.fechaProximoContenido = dto.fechaProximoContenido
        detalle.imagen = dto.imagen
        detalle.rating = dto.rating

        return itemDetailRepository.save(detalle)
    }

    // 3. Eliminar / Limpiar detalles de un ítem
    @Transactional
    fun eliminarDetalle(user: Usuario, listCod: String, itemCod: String) {
        validarPertenencia(user, listCod, itemCod)

        if (!itemDetailRepository.existsById(itemCod)) {
            throw IllegalArgumentException("No existen detalles que eliminar para el ID: $itemCod")
        }
        itemDetailRepository.deleteById(itemCod)
    }

    // 4. Autocompletado desde Internet
    @Transactional
    fun autocompletarDetalle(user: Usuario, listCod: String, itemCod: String, formatoSugerido: String?): ItemDetail {
        validarPertenencia(user, listCod, itemCod)

        val item = itemRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("El ítem con ID $itemCod no existe") }

        val detalleExistente = itemDetailRepository.findById(itemCod).orElse(null)
        val formatoFinal = formatoSugerido ?: detalleExistente?.formatoItem

        val regex = Regex("^(?:[0-9]+|[a-zA-Z])[.)-]\\s+|^[-*•]\\s+")
        val nombreLimpio = item.itemName.replace(regex, "").trim()

        val datosExternos = autoEnrichmentService.buscarDetallesAutomaticos(nombreLimpio, formatoFinal)
            ?: throw IllegalArgumentException("No se encontró información en internet para '$nombreLimpio' con formato '$formatoFinal'")

        val detalle = detalleExistente ?: ItemDetail(item = item)

        // Sobrescribir con los datos oficiales actualizados desde internet
        if (!datosExternos.formatoItem.isNullOrBlank()) {
            detalle.formatoItem = datosExternos.formatoItem
        } else if (detalle.formatoItem.isNullOrBlank()) {
            detalle.formatoItem = formatoFinal
        }
        detalle.cantidadEntregas = datosExternos.cantidadEntregas
        detalle.proximoContenido = datosExternos.proximoContenido
        detalle.fechaProximoContenido = datosExternos.fechaProximoContenido
        if (!datosExternos.imagen.isNullOrBlank()) {
            detalle.imagen = datosExternos.imagen
        }

        return itemDetailRepository.save(detalle)
    }
}