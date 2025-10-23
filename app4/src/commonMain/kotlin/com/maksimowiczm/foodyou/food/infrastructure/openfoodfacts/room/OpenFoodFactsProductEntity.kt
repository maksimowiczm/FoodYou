package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.OpenFoodFactsNutrients

@Entity(tableName = "OpenFoodFactsProduct")
data class OpenFoodFactsProductEntity(
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
