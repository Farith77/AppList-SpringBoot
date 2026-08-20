package com.dreamapps.AppList.repository

import com.dreamapps.AppList.entity.Lista
import com.dreamapps.AppList.entity.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ListaRepository : JpaRepository<Lista, String> {
    // 1. Para la pantalla principal (Listas activas del usuario)
    fun findByUserAndListActiveTrueOrderByListOrderAsc(user: Usuario): List<Lista>

    // 2. Para encontrar las listas de la papelera del usuario
    fun findByUserAndListActiveFalseOrderByListOrderAsc(user: Usuario): List<Lista>

    // 3. Para el historial completo del usuario
    fun findByUserOrderByListOrderAsc(user: Usuario): List<Lista>

    // 4. Para validar que una lista pertenece al usuario
    fun findByListCodAndUser(listCod: String, user: Usuario): Optional<Lista>
}