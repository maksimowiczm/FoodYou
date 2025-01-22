package com.maksimowiczm.foodyou.feature.product.data

import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.toDomain
import com.maksimowiczm.foodyou.feature.product.database.ProductDatabase

class ProductRepositoryImpl(
    productDatabase: ProductDatabase
) : ProductRepository {
    private val productDao = productDatabase.productDao()

    override suspend fun getProductById(id: Long): Product? {
        return productDao.getProductById(id)?.toDomain()
    }
}
