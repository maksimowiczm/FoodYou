package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data class SearchFoodCountQuery(
    val query: String?,
    val source: FoodSource.Type,
    val excludedRecipeId: FoodId.Recipe?,
) : Query

internal class SearchFoodCountQueryHandler(private val localFood: LocalFoodSearchDataSource) :
    QueryHandler<SearchFoodCountQuery, Int> {
    override val queryType: KClass<SearchFoodCountQuery>
        get() = SearchFoodCountQuery::class

    override fun handle(query: SearchFoodCountQuery): Flow<Int> =
        localFood.observeFoodCount(
            query = query.query,
            source = query.source,
            excludedRecipeId = query.excludedRecipeId?.id,
        )
}
