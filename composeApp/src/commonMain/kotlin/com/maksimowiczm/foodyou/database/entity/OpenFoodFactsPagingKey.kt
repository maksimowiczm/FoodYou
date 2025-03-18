package com.maksimowiczm.foodyou.database.entity

import androidx.room.Entity

@Entity(
    primaryKeys = [
        "queryString",
        "country"
    ]
)
data class OpenFoodFactsPagingKey(
    val queryString: String,
    val country: String,
    val fetchedCount: Int,
    val totalCount: Int
)
