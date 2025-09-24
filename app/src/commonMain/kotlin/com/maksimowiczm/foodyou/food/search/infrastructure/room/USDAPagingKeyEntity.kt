package com.maksimowiczm.foodyou.food.search.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "USDAPagingKey")
data class USDAPagingKeyEntity(
    @PrimaryKey val queryString: String,
    val fetchedCount: Int,
    val totalCount: Int,
)
