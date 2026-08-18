package com.dreamapps.AppList.repository

import com.dreamapps.AppList.entity.ItemDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemDetailRepository : JpaRepository<ItemDetail, String>