package com.maksimowiczm.foodyou.feature.food.diary.shared.usecase

import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import kotlinx.coroutines.flow.Flow

fun interface ObserveMealsUseCase {
    fun observe(): Flow<List<Meal>>
}

internal class ObserveMealsUseCaseImpl(private val queryBus: QueryBus) : ObserveMealsUseCase {
    override fun observe(): Flow<List<Meal>> = queryBus.dispatch(ObserveMealsQuery)
}
