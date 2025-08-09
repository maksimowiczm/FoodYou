package com.maksimowiczm.foodyou.feature.food.diary.search.usecase

import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodQuery
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.flow.Flow

internal fun interface ObserveRecipeUseCase {
    fun observe(id: FoodId.Recipe): Flow<Recipe?>
}

internal class ObserveRecipeUseCaseImpl(private val queryBus: QueryBus) : ObserveRecipeUseCase {
    override fun observe(id: FoodId.Recipe): Flow<Recipe?> =
        queryBus.dispatch<Recipe>(ObserveFoodQuery(id))
}
