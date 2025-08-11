package com.maksimowiczm.foodyou.business.food.infrastructure.persistence

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.network.RemoteMediatorFactory
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
internal interface LocalFoodSearchDataSource {

    fun search(
        query: QueryType,
        source: FoodSource.Type,
        config: PagingConfig,
        remoteMediatorFactory: RemoteMediatorFactory?,
        excludedRecipeId: Long?,
    ): Flow<PagingData<FoodSearch>>

    fun searchRecent(
        query: QueryType,
        config: PagingConfig,
        excludedRecipeId: Long?,
    ): Flow<PagingData<FoodSearch>>

    fun observeSearchHistory(limit: Int): Flow<List<SearchHistory>>

    suspend fun insertSearchHistory(entry: SearchHistory)

    fun observeFoodCount(
        query: QueryType,
        source: FoodSource.Type,
        excludedRecipeId: Long?,
    ): Flow<Int>

    fun observeRecentFoodCount(query: QueryType, excludedRecipeId: Long?): Flow<Int>
}
