package com.dreamapps.AppList.repository

import com.dreamapps.AppList.entity.Lista
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ListaRepository: JpaRepository<Lista, Int> {
    // aun en proceso
}