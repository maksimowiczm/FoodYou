package com.maksimowiczm.foodyou.feature.food.shared.usecase

import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodQuery
import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

fun interface ObserveFoodUseCase {
    fun observe(foodId: FoodId): Flow<Food?>
}

internal class ObserveFoodUseCaseImpl(private val queryBus: QueryBus) : ObserveFoodUseCase {
    override fun observe(foodId: FoodId): Flow<Food?> = queryBus.dispatch(ObserveFoodQuery(foodId))
}
