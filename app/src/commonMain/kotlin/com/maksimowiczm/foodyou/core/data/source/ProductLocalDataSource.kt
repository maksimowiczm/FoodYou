package com.maksimowiczm.foodyou.core.data.source

import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource
import kotlinx.coroutines.flow.Flow

interface ProductLocalDataSource {
    suspend fun upsertProduct(product: ProductEntity): Long
    suspend fun insertOpenFoodFactsProducts(products: List<ProductEntity>)
    fun observeProduct(id: Long): Flow<ProductEntity?>
    suspend fun deleteProduct(id: Long)
    suspend fun deleteUnusedProducts(source: ProductSource)
}
