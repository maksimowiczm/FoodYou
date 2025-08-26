package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

data class ObserveFoodQuery(val foodId: FoodId) : Query<Food?>

internal class ObserveFoodQueryHandler(
    private val productDataSource: LocalProductDataSource,
    private val recipeDataSource: LocalRecipeDataSource,
) : QueryHandler<ObserveFoodQuery, Food?> {
    override fun handle(query: ObserveFoodQuery): Flow<Food?> {
        val (foodId) = query

        return when (foodId) {
            is FoodId.Product -> productDataSource.observeProduct(foodId)
            is FoodId.Recipe -> recipeDataSource.observeRecipe(foodId)
        }
    }
}
