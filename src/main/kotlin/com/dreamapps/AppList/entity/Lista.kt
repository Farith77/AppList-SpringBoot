package com.dreamapps.AppList.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import com.github.f4b6a3.uuid.UuidCreator

@Entity
@Table(name = "lista")
class Lista(
    @Id
    @Column(name = "list_cod", length = 36)
    var listCod: String = UuidCreator.getTimeOrderedEpoch().toString(),

    @Column(name = "list_name", length = 100, nullable = false)
    var listName: String = "",

    @Column(name = "list_description", length = 250)
    var listDescription: String? = null,

    // Campos añadidos basados en los requisitos de Edu-01 y optimización
    @Column(name = "list_image", length = 200)
    var listImage: String? = null,

    @Column(name = "list_order")
    var listOrder: Int = 0,

    // Campo añadido para sincronizar con la Papelera de Android (Eliminado lógico)
    @Column(name = "list_active")
    var listActive: Boolean = true,

    // Relación con el Usuario dueño de la lista
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: Usuario? = null,

    // RELACIÓN EN CASCADA
    @OneToMany(mappedBy = "lista", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<Item> = mutableListOf()
)