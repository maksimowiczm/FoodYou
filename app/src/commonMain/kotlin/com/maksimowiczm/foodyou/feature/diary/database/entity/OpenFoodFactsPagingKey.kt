package com.maksimowiczm.foodyou.feature.diary.database.entity

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
