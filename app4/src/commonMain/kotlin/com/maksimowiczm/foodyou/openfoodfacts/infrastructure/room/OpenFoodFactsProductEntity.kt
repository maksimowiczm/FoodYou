package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.OpenFoodFactsNutrients

@Entity(tableName = "OpenFoodFactsProduct")
internal data class OpenFoodFactsProductEntity(
    @PrimaryKey val barcode: String,
    val names: String,
    val url: String?,
    @Embedded val nutrients: OpenFoodFactsNutrients?,
    val brand: String?,
    val packageWeight: Double?,
    val packageQuantityUnit: String?,
    val servingWeight: Double?,
    val servingQuantityUnit: String?,
    val thumbnailUrl: String?,
    val imageUrl: String?,
)
