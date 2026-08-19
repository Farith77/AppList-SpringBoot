package com.dreamapps.AppList.service

import com.dreamapps.AppList.dto.ItemDetailDto
import com.dreamapps.AppList.entity.ItemDetail
import com.dreamapps.AppList.repository.ItemDetailRepository
import com.dreamapps.AppList.repository.ItemRepository
import com.dreamapps.AppList.service.external.AutoEnrichmentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemDetailService(
    private val itemDetailRepository: ItemDetailRepository,
    private val itemRepository: ItemRepository,
    private val autoEnrichmentService: AutoEnrichmentService
) {

    // 1. Obtener detalles de un ítem
    fun obtenerDetalle(itemCod: String): ItemDetail {
        return itemDetailRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("No se encontraron detalles para el ítem con ID: $itemCod") }
    }

    // 2. Crear o Actualizar detalles manualmente
    @Transactional
    fun guardarOActualizarDetalle(dto: ItemDetailDto): ItemDetail {
        val item = itemRepository.findById(dto.itemCod)
            .orElseThrow { IllegalArgumentException("El ítem con ID ${dto.itemCod} no existe") }

        val detalle = itemDetailRepository.findById(dto.itemCod)
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
    fun eliminarDetalle(itemCod: String) {
        if (!itemDetailRepository.existsById(itemCod)) {
            throw IllegalArgumentException("No existen detalles que eliminar para el ID: $itemCod")
        }
        itemDetailRepository.deleteById(itemCod)
    }

    // 4. Autocompletado desde Internet
    @Transactional
    fun autocompletarDetalle(itemCod: String, formatoSugerido: String?): ItemDetail {
        val item = itemRepository.findById(itemCod)
            .orElseThrow { IllegalArgumentException("El ítem con ID $itemCod no existe") }

        val detalleExistente = itemDetailRepository.findById(itemCod).orElse(null)
        val formatoFinal = formatoSugerido ?: detalleExistente?.formatoItem

        // ====================================================================
        // MAGIA DE LIMPIEZA (Regex)
        // Eliminamos "1. ", "a) ", "- ", etc. para que la API no se confunda
        // ====================================================================
        val regex = Regex("^(?:[0-9]+|[a-zA-Z])[.)-]\\s+|^[-*•]\\s+")
        val nombreLimpio = item.itemName.replace(regex, "").trim()

        // Pasamos el nombreLimpio al servicio externo en lugar de item.itemName
        val datosExternos = autoEnrichmentService.buscarDetallesAutomaticos(nombreLimpio, formatoFinal)
            ?: throw IllegalArgumentException("No se encontró información en internet para '$nombreLimpio' con formato '$formatoFinal'")

        val detalle = detalleExistente ?: ItemDetail(item = item)

        // Rellenamos solo los espacios que estén vacíos/null
        if (detalle.formatoItem.isNullOrBlank()) detalle.formatoItem = datosExternos.formatoItem
        if (detalle.cantidadEntregas == null) detalle.cantidadEntregas = datosExternos.cantidadEntregas
        if (detalle.proximoContenido == false || detalle.proximoContenido == null) detalle.proximoContenido = datosExternos.proximoContenido
        if (detalle.fechaProximoContenido == null) detalle.fechaProximoContenido = datosExternos.fechaProximoContenido
        if (detalle.imagen.isNullOrBlank()) detalle.imagen = datosExternos.imagen

        return itemDetailRepository.save(detalle)
    }
}