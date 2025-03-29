package com.maksimowiczm.foodyou.feature.diary.data

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.DiarySearchModel
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SearchRepository {
    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>

    fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<DiarySearchModel>>

    fun queryProducts(query: String?): Flow<PagingData<DiarySearchModel>>
}
