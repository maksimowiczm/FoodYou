package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class SearchRecentFoodCount(val query: String?, val excludedRecipeId: FoodId.Recipe?) : Query

internal class SearchRecentFoodCountQueryHandler : QueryHandler<SearchRecentFoodCount, Int> {
    override val queryType: KClass<SearchRecentFoodCount>
        get() = SearchRecentFoodCount::class

    override fun handle(query: SearchRecentFoodCount): Flow<Int> {
        // TODO
        return flowOf(0)
    }
}
