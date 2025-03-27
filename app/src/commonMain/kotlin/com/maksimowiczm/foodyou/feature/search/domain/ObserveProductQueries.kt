package com.maksimowiczm.foodyou.feature.search.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

data class ProductQuery(val query: String, val date: LocalDateTime)

interface ObserveProductQueries {
    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>
    operator fun invoke(limit: Int): Flow<List<ProductQuery>> = observeProductQueries(limit)
}
