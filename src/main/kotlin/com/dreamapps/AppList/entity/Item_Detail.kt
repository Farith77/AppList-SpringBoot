package com.dreamapps.AppList.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "item_detail")
class ItemDetail(
    // Clave Primaria que a la vez es Clave Foránea con Item (Relación 1 a 1 perfecta)
    @Id
    @Column(name = "item_cod", length = 36)
    var itemCod: String? = null,

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "item_cod")
    var item: Item? = null,

    @Column(name = "formato_item", length = 50)
    var formatoItem: String? = null,

    @Column(name = "cantidad_entregas")
    var cantidadEntregas: Int? = null,

    @Column(name = "proximo_contenido")
    var proximoContenido: Boolean? = false,

    @Column(name = "fecha_proximo_contenido")
    var fechaProximoContenido: LocalDate? = null,

    @Column(name = "imagen", length = 500)
    var imagen: String? = null,

    @Column(name = "rating")
    var rating: Int? = null
)