package com.maksimowiczm.foodyou.feature.openfoodfacts.data

import com.maksimowiczm.foodyou.core.data.model.product.ProductSource
import com.maksimowiczm.foodyou.core.data.source.ProductLocalDataSource

internal class ProductRepository(private val productDao: ProductLocalDataSource) {

    suspend fun deleteUnusedOpenFoodFactsProducts() {
        productDao.deleteUnusedProducts(ProductSource.OpenFoodFacts)
    }
}
