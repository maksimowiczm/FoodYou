package com.maksimowiczm.foodyou.feature.diary.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchRecipeEntry
import kotlinx.coroutines.flow.Flow

fun interface QueryRecipeProductsUseCase {
    fun queryProducts(query: String?, recipeId: Long): Flow<PagingData<SearchRecipeEntry>>

    operator fun invoke(query: String?, recipeId: Long): Flow<PagingData<SearchRecipeEntry>> =
        queryProducts(query, recipeId)
}
