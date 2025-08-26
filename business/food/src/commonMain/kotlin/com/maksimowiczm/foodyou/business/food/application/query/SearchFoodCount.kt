package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.queryType
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

data class SearchFoodCountQuery(
    val query: String?,
    val source: FoodSource.Type,
    val excludedRecipeId: FoodId.Recipe?,
) : Query<Int>

internal class SearchFoodCountQueryHandler(private val localFood: LocalFoodSearchDataSource) :
    QueryHandler<SearchFoodCountQuery, Int> {

    override fun handle(query: SearchFoodCountQuery): Flow<Int> =
        localFood.observeFoodCount(
            query = queryType(query.query),
            source = query.source,
            excludedRecipeId = query.excludedRecipeId?.id,
        )
}
