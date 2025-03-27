package com.maksimowiczm.foodyou.feature.search.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class Product(val id: Long, val name: String, val brand: String?)

interface QueryProductsUseCase {
    fun queryProducts(query: String?): Flow<PagingData<Product>>
}
