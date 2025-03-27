package com.maksimowiczm.foodyou.feature.search.database.entity

import androidx.room.Entity

@Entity(
    primaryKeys = [
        "queryString",
        "country"
    ]
)
data class OpenFoodFactsPagingKeyEntity(
    val queryString: String,
    val country: String,
    val fetchedCount: Int,
    val totalCount: Int
)
