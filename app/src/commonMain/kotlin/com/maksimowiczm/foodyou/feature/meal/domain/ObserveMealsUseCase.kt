package com.maksimowiczm.foodyou.feature.meal.domain

import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.ext.mapValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

internal data class Meal(val id: Long, val from: LocalTime, val to: LocalTime, val name: String) {
    val isAllDay: Boolean
        get() = from == to
}

/**
 * Use case for observing meals in meals settings. Meals are sorted by rank.
 */
internal interface ObserveMealsUseCase {
    operator fun invoke(): Flow<List<Meal>>
}

internal class ObserveMealsUseCaseImpl(private val mealRepository: MealRepository) :
    ObserveMealsUseCase {
    override fun invoke() = mealRepository
        .observeMeals()
        .map { list ->
            list.sortedBy { it.rank }
        }
        .mapValues {
            Meal(
                id = it.id,
                name = it.name,
                from = it.from,
                to = it.to
            )
        }
}
