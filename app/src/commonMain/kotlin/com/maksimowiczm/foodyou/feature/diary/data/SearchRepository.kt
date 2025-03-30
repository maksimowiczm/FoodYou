package com.maksimowiczm.foodyou.feature.diary.data

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SearchRepository {
    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>

    fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<ProductWithMeasurement>>
}
