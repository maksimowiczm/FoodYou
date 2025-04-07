package com.maksimowiczm.foodyou.feature.diary.core.database.product

import androidx.room.TypeConverter

@Suppress("unused")
class ProductSourceConverter {
    @TypeConverter
    fun fromProductSource(productSource: ProductSource): Int = when (productSource) {
        ProductSource.User -> 0
        ProductSource.OpenFoodFacts -> 1
    }

    @TypeConverter
    fun toProductSource(productSource: Int): ProductSource = when (productSource) {
        0 -> ProductSource.User
        1 -> ProductSource.OpenFoodFacts
        else -> throw IllegalArgumentException("ProductSource not found")
    }
}
