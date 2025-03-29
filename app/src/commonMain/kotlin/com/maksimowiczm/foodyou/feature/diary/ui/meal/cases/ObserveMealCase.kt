package com.maksimowiczm.foodyou.feature.diary.ui.meal.cases

import com.maksimowiczm.foodyou.ext.combine
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.MealFoodListItem
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveMealCase(
    private val stringFormatRepository: StringFormatRepository,
    private val measurementRepository: MeasurementRepository,
    private val mealRepository: MealRepository,
    private val productRepository: ProductRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(mealId: Long, date: LocalDate): Flow<Meal?> {
        return mealRepository.observeMealById(mealId).flatMapLatest { meal ->
            if (meal == null) {
                return@flatMapLatest flowOf(null)
            }

            measurementRepository.observeMeasurements(mealId, date).flatMapLatest { measurements ->
                if (measurements.isEmpty()) {
                    return@flatMapLatest flowOf(
                        Meal.empty(
                            id = meal.id,
                            name = meal.name,
                            date = stringFormatRepository.formatDate(date),
                            from = stringFormatRepository.formatTime(meal.from),
                            to = stringFormatRepository.formatTime(meal.to),
                            isAllDay = meal.isAllDay
                        )
                    )
                }

                val productFlows = measurements
                    .mapNotNull { it.foodId as? FoodId.Product }
                    .map { it.productId }
                    .map { productRepository.observeProductById(it).filterNotNull() }

                measurements.zip(productFlows) { measurement, productFlow ->
                    productFlow.map { product ->
                        val weight = measurement.measurement.getWeight(product)
                        val calories = weight * product.nutrients.calories / 100
                        val proteins = weight * product.nutrients.proteins / 100
                        val carbohydrates = weight * product.nutrients.carbohydrates / 100
                        val fats = weight * product.nutrients.fats / 100

                        MealFoodListItem(
                            measurementId = measurement.measurementId,
                            name = product.name,
                            brand = product.brand,
                            calories = calories.roundToInt(),
                            proteins = proteins.roundToInt(),
                            carbohydrates = carbohydrates.roundToInt(),
                            fats = fats.roundToInt(),
                            weightMeasurement = measurement.measurement,
                            weight = measurement.measurement.getWeight(product)
                        )
                    }
                }.combine { foodItems ->
                    Meal(
                        id = meal.id,
                        name = meal.name,
                        date = stringFormatRepository.formatDate(date),
                        from = stringFormatRepository.formatTime(meal.from),
                        to = stringFormatRepository.formatTime(meal.to),
                        isAllDay = meal.isAllDay,
                        foods = foodItems.toList(),
                        calories = foodItems.sumOf { it.calories },
                        proteins = foodItems.sumOf { it.proteins },
                        carbohydrates = foodItems.sumOf { it.carbohydrates },
                        fats = foodItems.sumOf { it.fats }
                    )
                }
            }
        }
    }
}
