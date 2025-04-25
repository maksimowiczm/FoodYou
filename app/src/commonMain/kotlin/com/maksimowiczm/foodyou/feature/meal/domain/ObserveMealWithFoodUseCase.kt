package com.maksimowiczm.foodyou.feature.meal.domain

import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.sumOf
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal data class MealWithFood(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val from: LocalTime,
    val to: LocalTime,
    val foods: List<MealFood>
) {
    val isAllDay: Boolean
        get() = from == to

    val isEmpty: Boolean
        get() = foods.isEmpty()

    val calories: Int
        get() = foods.sumOf {
            val weight = it.weight ?: return@sumOf 0f
            it.food.nutrients.calories.value * weight / 100f
        }.roundToInt()

    val proteins: Int
        get() = foods.sumOf {
            val weight = it.weight ?: return@sumOf 0f
            it.food.nutrients.proteins.value * weight / 100f
        }.roundToInt()

    val carbohydrates: Int
        get() = foods.sumOf {
            val weight = it.weight ?: return@sumOf 0f
            it.food.nutrients.carbohydrates.value * weight / 100f
        }.roundToInt()

    val fats: Int
        get() = foods.sumOf {
            val weight = it.weight ?: return@sumOf 0f
            it.food.nutrients.fats.value * weight / 100f
        }.roundToInt()
}

internal data class MealFood(
    val measurementId: MeasurementId,
    val measurement: Measurement,
    val food: Food
) {
    val weight: Float?
        get() = measurement.weight(food)
}

internal fun interface ObserveMealWithFoodUseCase {
    operator fun invoke(mealId: Long, date: LocalDate): Flow<MealWithFood>
}

internal class ObserveMealWithFoodUseCaseImpl(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository
) : ObserveMealWithFoodUseCase {
    override fun invoke(mealId: Long, date: LocalDate): Flow<MealWithFood> = combine(
        mealRepository.observeMeal(
            id = mealId
        ).filterNotNull(),
        measurementRepository.observeMeasurements(
            mealId = mealId,
            date = date
        )
    ) { meal, measurements ->
        val food = measurements.map { measurement ->
            MealFood(
                measurementId = measurement.measurementId,
                measurement = measurement.measurement,
                food = measurement.food
            )
        }

        MealWithFood(
            id = meal.id,
            name = meal.name,
            date = date,
            from = meal.from,
            to = meal.to,
            foods = food
        )
    }
}
