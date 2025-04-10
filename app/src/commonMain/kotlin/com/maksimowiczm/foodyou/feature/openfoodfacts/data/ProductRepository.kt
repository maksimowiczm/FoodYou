package com.maksimowiczm.foodyou.feature.openfoodfacts.data

import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.entity.ProductSource

internal class ProductRepository(database: FoodYouDatabase) {
    private val productDao = database.productDao

    suspend fun deleteUnusedOpenFoodFactsProducts() {
        productDao.deleteUnusedProducts(ProductSource.OpenFoodFacts)
    }
}
