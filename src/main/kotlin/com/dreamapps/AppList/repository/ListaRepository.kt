package com.dreamapps.AppList.repository

import com.dreamapps.AppList.entity.Lista
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ListaRepository: JpaRepository<Lista, String> {
    // 1. Para la pantalla principal (Listas normales)
    fun findByListActiveTrue(): List<Lista>

    // 2. NUEVO: Para encontrar las listas de la papelera antes de borrarlas
    fun findByListActiveFalse(): List<Lista>

    // NOTA: Eliminamos `fun deleteByListActiveFalse()` porque las consultas
    // de borrado directo en Spring Data JPA ignoran el CascadeType.ALL.
}