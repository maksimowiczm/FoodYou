package com.maksimowiczm.foodyou.foodyou3.domain.food

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3FoodSource
import kotlinx.coroutines.flow.Flow

interface FoodYou3FoodSearchRepository {
    fun search(
        query: SearchQuery,
        source: FoodYou3FoodSource.Type? = null,
        config: PagingConfig,
    ): Flow<PagingData<FoodYou3FoodSearch>>

    fun searchFoodCount(
        query: SearchQuery,
        source: FoodYou3FoodSource.Type? = null,
    ): Flow<Int>
}
