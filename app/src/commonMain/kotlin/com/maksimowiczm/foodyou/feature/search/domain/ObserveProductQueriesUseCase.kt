package com.maksimowiczm.foodyou.feature.search.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

data class ProductQuery(val query: String, val date: LocalDateTime)

interface ObserveProductQueriesUseCase {
    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>
}
