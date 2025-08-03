package com.maksimowiczm.foodyou.feature.food.data.database.usda

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class USDAPagingKey(
    @PrimaryKey
    val queryString: String,
    val fetchedCount: Int,
    val totalCount: Int
)
