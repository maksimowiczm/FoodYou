package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductLocalDataSource {
    suspend fun getProducts(): List<ProductEntity>
    suspend fun updateProduct(product: ProductEntity)
    suspend fun insertProduct(product: ProductEntity): Long
    fun observeProduct(id: Long): Flow<ProductEntity?>
    suspend fun deleteProduct(id: Long)
}
