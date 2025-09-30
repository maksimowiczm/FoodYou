package com.maksimowiczm.foodyou.food.infrastructure.room

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = ProductEntity::class)
@Entity(tableName = "ProductFts")
data class ProductFts(val name: String, val brand: String?, val note: String?)
