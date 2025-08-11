package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.queryType
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data class SearchRecentFoodCount(val query: String?, val excludedRecipeId: FoodId.Recipe?) :
    Query<Int>

internal class SearchRecentFoodCountQueryHandler(
    private val foodSearchSource: LocalFoodSearchDataSource
) : QueryHandler<SearchRecentFoodCount, Int> {

    override fun handle(query: SearchRecentFoodCount): Flow<Int> {
        val (queryText, excludedRecipeId) = query

        return foodSearchSource.observeRecentFoodCount(queryType(queryText), excludedRecipeId?.id)
    }
}
