package com.maksimowiczm.foodyou.feature.diary.ui.mealscard.cases

import com.maksimowiczm.foodyou.ext.sumOf
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryMeasuredProduct
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.model.Meal
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveMealsByDateCase(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate): Flow<List<Meal>> =
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
    measurements: List<DiaryMeasuredProduct>
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
