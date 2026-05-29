package com.dreamapps.AppList.entity

import jakarta.persistence.*

@Entity
@Table(name = "lista")
class Lista(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental
    @Column(name = "list_cod")
    val listCod: Int = 0,  

    @Column(name = "list_name", length = 100, nullable = false)
    var listName: String,

    @Column(name = "list_description", length = 250)
    var listDescription: String? = null,

    // Campos añadidos basados en los requisitos de Edu-01 y optimización
    @Column(name = "list_image", length = 200)
    var listImage: String? = null,

    @Column(name = "list_order")
    var listOrder: Int = 0
)