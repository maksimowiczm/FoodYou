package com.maksimowiczm.foodyou.feature.diary.domain

import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import kotlinx.coroutines.flow.Flow

fun interface ObserveProductQueriesUseCase {
    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>
}
