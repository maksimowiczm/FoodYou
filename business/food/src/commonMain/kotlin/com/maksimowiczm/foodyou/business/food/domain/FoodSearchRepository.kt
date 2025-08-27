package com.maksimowiczm.foodyou.business.food.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

interface FoodSearchRepository {
    fun searchFood(
        query: QueryType,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>

    fun observeSearchFoodCount(
        query: QueryType,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int>

    fun searchRecentFood(
        query: QueryType,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>

    fun observeRecentFoodCount(query: QueryType, excludedRecipeId: FoodId.Recipe?): Flow<Int>
}
