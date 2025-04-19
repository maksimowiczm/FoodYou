package com.maksimowiczm.foodyou.core.data.database.product

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource

@Suppress("unused")
class ProductSourceConverter {
    @TypeConverter
    fun fromProductSource(productSource: ProductSource): Int = when (productSource) {
        ProductSource.User -> ProductSourceSQLConstants.USER
        ProductSource.OpenFoodFacts -> ProductSourceSQLConstants.OPEN_FOOD_FACTS
    }

    @TypeConverter
    fun toProductSource(productSource: Int): ProductSource = when (productSource) {
        ProductSourceSQLConstants.USER -> ProductSource.User
        ProductSourceSQLConstants.OPEN_FOOD_FACTS -> ProductSource.OpenFoodFacts
        else -> error("ProductSource not found")
    }
}

object ProductSourceSQLConstants {
    const val USER = 0
    const val OPEN_FOOD_FACTS = 1
}
