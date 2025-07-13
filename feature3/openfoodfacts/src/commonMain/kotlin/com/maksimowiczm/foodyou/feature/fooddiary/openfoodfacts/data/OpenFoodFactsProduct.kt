package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsNutrients

@Entity
data class OpenFoodFactsProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val packageWeight: Float?,
    val servingWeight: Float?,
    @Embedded
    val nutritionFacts: OpenFoodFactsNutrients?,
    val downloadedAtEpochSeconds: Long
) {
    val url: String?
        get() = barcode?.let {
            "https://world.openfoodfacts.org/product/$it"
        }
}
