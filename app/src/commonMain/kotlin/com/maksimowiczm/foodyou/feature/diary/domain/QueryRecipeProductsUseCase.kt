package com.maksimowiczm.foodyou.feature.diary.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import kotlinx.coroutines.flow.Flow

fun interface QueryRecipeProductsUseCase {
    fun queryProducts(query: String?, recipeId: Long): Flow<PagingData<ProductWithMeasurement>>

    operator fun invoke(query: String?, recipeId: Long): Flow<PagingData<ProductWithMeasurement>> =
        queryProducts(query, recipeId)
}
