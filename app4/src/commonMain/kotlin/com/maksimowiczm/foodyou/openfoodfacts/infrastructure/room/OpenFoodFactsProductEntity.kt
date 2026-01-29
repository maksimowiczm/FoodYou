package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OpenFoodFactsProduct")
internal data class OpenFoodFactsProductEntity(
    @PrimaryKey val barcode: String,
    val rawJson: String,
)
