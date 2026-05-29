package com.dreamapps.AppList.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
@Table(name = "item")
class Item(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_cod")
    val itemCod: Int = 0,

    // longitud de 250 para permitir nombres/frases reales
    @Column(name = "item_name", length = 250, nullable = false)
    var itemName: String,

    // Agregamos el orden
    @Column(name = "item_order")
    var itemOrder: Int = 0,

    // --- RELACIÓN DE LLAVE FORÁNEA (FK) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_cod")
    @JsonIgnore
    var lista: Lista? = null
)