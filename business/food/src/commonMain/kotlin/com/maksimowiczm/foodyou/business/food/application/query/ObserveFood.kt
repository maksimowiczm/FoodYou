package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data class ObserveFoodQuery(val foodId: FoodId) : Query

internal class ObserveFoodQueryHandler(
    private val productDataSource: LocalProductDataSource,
    private val recipeDataSource: LocalRecipeDataSource,
) : QueryHandler<ObserveFoodQuery, Food?> {
    override val queryType = ObserveFoodQuery::class

    override fun handle(query: ObserveFoodQuery): Flow<Food?> {
        val (foodId) = query

        return when (foodId) {
            is FoodId.Product -> productDataSource.observeProduct(foodId)
            is FoodId.Recipe -> recipeDataSource.observeRecipe(foodId)
        }
    }
}
