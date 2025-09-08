package com.maksimowiczm.foodyou.app.infrastructure.room.openfoodfacts

import androidx.room.Entity

@Entity(tableName = "OpenFoodFactsPagingKey", primaryKeys = ["queryString", "country"])
data class OpenFoodFactsPagingKeyEntity(
    val queryString: String,
    val country: String,
    val fetchedCount: Int,
    val totalCount: Int,
)
