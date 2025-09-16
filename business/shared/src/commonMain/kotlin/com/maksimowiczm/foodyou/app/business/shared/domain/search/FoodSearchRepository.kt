package com.maksimowiczm.foodyou.app.business.shared.domain.search

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.domain.search.SearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface FoodSearchRepository {
    fun search(
        query: SearchQuery,
        source: FoodSource.Type,
        config: PagingConfig,
        remoteMediatorFactory: RemoteMediatorFactory?,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>

    fun searchRecent(
        query: SearchQuery,
        config: PagingConfig,
        now: LocalDateTime,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>

    fun searchFoodCount(
        query: SearchQuery,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int>

    fun searchRecentFoodCount(
        query: SearchQuery,
        now: LocalDateTime,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int>
}
