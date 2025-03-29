package com.maksimowiczm.foodyou.feature.diary.ui.mealscard.cases

import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.model.Meal
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveMealsByDateCase(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    private val productRepository: ProductRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate): Flow<List<Meal>> =
        mealRepository.observeMeals().flatMapLatest { meals ->
            val flows = meals.map { meal ->
                measurementRepository
                    .observeMeasurements(meal.id, date)
                    .flatMapLatest { measurements ->
                        meal.toUseCaseModel(measurements)
                    }
            }

            combine(flows) {
                it.toList()
            }
        }

    private fun com.maksimowiczm.foodyou.feature.diary.data.model.Meal.toUseCaseModel(
        measurements: List<FoodMeasurement>
    ): Flow<Meal> {
        if (measurements.isEmpty()) {
            return flowOf(
                Meal(
                    id = id,
                    name = name,
                    from = from,
                    to = to,
                    rank = rank,
                    calories = 0,
                    proteins = 0,
                    carbohydrates = 0,
                    fats = 0,
                    isEmpty = true
                )
            )
        }

        val productFlows = measurements
            .map { it.foodId }
            .filterIsInstance<FoodId.Product>()
            .map { it.productId }
            .map { productRepository.observeProductById(it).filterNotNull() }

        return combine(productFlows) { products ->
            val calories = measurements.zip(products).map { (measurement, product) ->
                val weight = measurement.measurement.getWeight(product)
                product.nutrients.calories * weight / 100
            }.sum()

            val proteins = measurements.zip(products).map { (measurement, product) ->
                val weight = measurement.measurement.getWeight(product)
                product.nutrients.proteins * weight / 100
            }.sum()

            val carbohydrates = measurements.zip(products).map { (measurement, product) ->
                val weight = measurement.measurement.getWeight(product)
                product.nutrients.carbohydrates * weight / 100
            }.sum()

            val fats = measurements.zip(products).map { (measurement, product) ->
                val weight = measurement.measurement.getWeight(product)
                product.nutrients.fats * weight / 100
            }.sum()

            Meal(
                id = id,
                name = name,
                from = from,
                to = to,
                rank = rank,
                calories = calories.roundToInt(),
                proteins = proteins.roundToInt(),
                carbohydrates = carbohydrates.roundToInt(),
                fats = fats.roundToInt(),
                isEmpty = false
            )
        }
    }
}
