package com.maksimowiczm.foodyou.feature.search.domain

import com.maksimowiczm.foodyou.feature.search.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ObserveProductUseCase {
    fun observeProduct(id: Long): Flow<Product>
}
