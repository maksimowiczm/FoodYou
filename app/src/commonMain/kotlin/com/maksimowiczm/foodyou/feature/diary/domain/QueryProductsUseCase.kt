package com.maksimowiczm.foodyou.feature.diary.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchDiaryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

fun interface QueryProductsUseCase {
    fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<SearchDiaryEntry>>

    operator fun invoke(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<SearchDiaryEntry>> = queryProducts(mealId, date, query)
}
