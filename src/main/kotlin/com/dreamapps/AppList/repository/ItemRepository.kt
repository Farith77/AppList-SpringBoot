package com.dreamapps.AppList.repository

import com.dreamapps.AppList.entity.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, String> {
    fun findByListaListCodOrderByItemOrderAsc(listCod: String): List<Item>
}