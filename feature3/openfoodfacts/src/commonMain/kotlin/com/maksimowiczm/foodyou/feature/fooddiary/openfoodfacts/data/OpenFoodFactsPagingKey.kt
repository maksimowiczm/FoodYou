package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data

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
