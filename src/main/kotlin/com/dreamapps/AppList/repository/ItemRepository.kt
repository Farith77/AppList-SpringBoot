package com.dreamapps.AppList.repository

import com.dreamapps.AppList.entity.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Int> {

    // Spring Boot leerá este nombre y creará un:
    // SELECT * FROM item WHERE list_cod = ? ORDER BY item_order ASC
    fun findByListaListCodOrderByItemOrderAsc(listCod: Int): List<Item>
}