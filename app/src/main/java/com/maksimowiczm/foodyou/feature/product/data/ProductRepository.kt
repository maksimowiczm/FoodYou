package com.maksimowiczm.foodyou.feature.product.data

import com.maksimowiczm.foodyou.feature.product.data.model.Product

interface ProductRepository {
    suspend fun getProductById(id: Long): Product?
}
