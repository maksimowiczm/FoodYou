package com.maksimowiczm.foodyou.feature.diary.ui.meal.cases

import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.MealFoodListItem
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveMealCase(
    private val stringFormatRepository: StringFormatRepository,
    private val measurementRepository: MeasurementRepository,
    private val mealRepository: MealRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(mealId: Long, date: LocalDate): Flow<Meal?> {
        return mealRepository.observeMealById(mealId).flatMapLatest { meal ->
            if (meal == null) {
                return@flatMapLatest flowOf(null)
            }

            measurementRepository.observeMeasurements(mealId, date).map {
                val foods = it.map {
                    MealFoodListItem(
                        measurementId = it.measurementId,
                        name = it.product.name,
                        brand = it.product.brand,
                        calories = it.calories.roundToInt(),
                        proteins = it.proteins.roundToInt(),
                        carbohydrates = it.carbohydrates.roundToInt(),
                        fats = it.fats.roundToInt(),
                        weightMeasurement = it.measurement,
                        weight = it.measurement.getWeight(it.product)
                    )
                }

                Meal(
                    id = meal.id,
                    name = meal.name,
                    date = stringFormatRepository.formatDate(date),
                    from = stringFormatRepository.formatTime(meal.from),
                    to = stringFormatRepository.formatTime(meal.to),
                    isAllDay = meal.isAllDay,
                    foods = foods,
                    calories = it.map { it.calories }.sum().roundToInt(),
                    proteins = it.map { it.proteins }.sum().roundToInt(),
                    carbohydrates = it.map { it.carbohydrates }.sum().roundToInt(),
                    fats = it.map { it.fats }.sum().roundToInt()
                )
            }
        }
    }
}
