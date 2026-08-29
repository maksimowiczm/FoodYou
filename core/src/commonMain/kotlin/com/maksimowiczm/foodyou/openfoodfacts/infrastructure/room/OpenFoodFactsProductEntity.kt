package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.room3.*

@Entity(tableName = "OpenFoodFactsProduct")
internal data class OpenFoodFactsProductEntity(@PrimaryKey val barcode: String, val rawJson: String)
