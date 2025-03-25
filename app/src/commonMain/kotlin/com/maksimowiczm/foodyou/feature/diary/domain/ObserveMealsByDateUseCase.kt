package com.maksimowiczm.foodyou.feature.diary.domain

import com.maksimowiczm.foodyou.ext.sumOf
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int,
    val isEmpty: Boolean
) {
    val isAllDay: Boolean
        get() = from == to
}

fun interface ObserveMealsByDateUseCase {
    operator fun invoke(date: LocalDate): Flow<List<Meal>>
}

class ObserveMealsByDateUseCaseImpl(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository
) : ObserveMealsByDateUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(date: LocalDate): Flow<List<Meal>> =
        mealRepository.observeMeals().flatMapLatest { meals ->
            val flows = meals.map { meal ->
                measurementRepository
                    .observeMeasurements(meal.id, date)
                    .map { measurements ->
                        meal.toUseCaseModel(measurements)
                    }
            }

            combine(flows) {
                it.toList()
            }
        }
}

private fun com.maksimowiczm.foodyou.feature.diary.data.model.Meal.toUseCaseModel(
    measurements: List<ProductWithMeasurement.Measurement>
) = Meal(
    id = id,
    name = name,
    from = from,
    to = to,
    rank = rank,
    calories = measurements.sumOf { it.calories }.roundToInt(),
    proteins = measurements.sumOf { it.proteins }.roundToInt(),
    carbohydrates = measurements.sumOf { it.carbohydrates }.roundToInt(),
    fats = measurements.sumOf { it.fats }.roundToInt(),
    isEmpty = measurements.isEmpty()
)
