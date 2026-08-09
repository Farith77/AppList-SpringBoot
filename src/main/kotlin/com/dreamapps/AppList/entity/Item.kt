package com.dreamapps.AppList.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.f4b6a3.uuid.UuidCreator

@Entity
@Table(name = "item")
class Item(
    @Id
    @Column(name = "item_cod", length = 36)
    var itemCod: String = UuidCreator.getTimeOrderedEpoch().toString(),

    // longitud de 250 para permitir nombres/frases reales
    @Column(name = "item_name", length = 250, nullable = false)
    var itemName: String = "",

    // Agregamos el orden
    @Column(name = "item_order")
    var itemOrder: Int = 0,

    // --- RELACIÓN DE LLAVE FORÁNEA (FK) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_cod")
    @JsonIgnore
    var lista: Lista? = null
)