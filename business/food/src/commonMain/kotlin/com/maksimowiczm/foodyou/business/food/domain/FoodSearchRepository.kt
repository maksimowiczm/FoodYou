package com.maksimowiczm.foodyou.business.food.domain

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.business.shared.domain.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface FoodSearchRepository {
    fun search(
        query: QueryType,
        source: FoodSource.Type,
        config: PagingConfig,
        remoteMediatorFactory: RemoteMediatorFactory?,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>

    fun searchRecent(
        query: QueryType,
        config: PagingConfig,
        now: LocalDateTime,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>

    fun searchFoodCount(
        query: QueryType,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int>

    fun searchRecentFoodCount(
        query: QueryType,
        now: LocalDateTime,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int>
}
