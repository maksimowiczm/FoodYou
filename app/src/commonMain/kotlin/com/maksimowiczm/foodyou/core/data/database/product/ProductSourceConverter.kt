package com.maksimowiczm.foodyou.core.data.database.product

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource

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
