package com.maksimowiczm.foodyou.business.food.application.query

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class SearchRecentFoodQuery(val query: String?, val excludedRecipeId: FoodId.Recipe?) :
    Query<PagingData<FoodSearch>>

internal class SearchRecentFoodQueryHandler() :
    QueryHandler<SearchRecentFoodQuery, PagingData<FoodSearch>> {

    override fun handle(query: SearchRecentFoodQuery): Flow<PagingData<FoodSearch>> {
        // TODO
        return flowOf(PagingData.empty())
    }
}
