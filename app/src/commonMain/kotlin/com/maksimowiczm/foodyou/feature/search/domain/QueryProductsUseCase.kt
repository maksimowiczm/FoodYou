package com.maksimowiczm.foodyou.feature.search.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.search.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface QueryProductsUseCase {
    fun queryProducts(query: String?): Flow<PagingData<Product>>
}
