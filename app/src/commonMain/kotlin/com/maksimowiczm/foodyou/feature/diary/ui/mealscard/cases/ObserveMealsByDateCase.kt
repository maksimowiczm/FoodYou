package com.maksimowiczm.foodyou.feature.diary.ui.mealscard.cases

import com.maksimowiczm.foodyou.ext.combine
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal as DomainModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.model.Meal
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate

class ObserveMealsByDateCase(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    private val productRepository: ProductRepository,
    private val stringFormatRepository: StringFormatRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate): Flow<List<Meal>> =
        mealRepository.observeMeals().flatMapLatest { meals ->
            meals.map { meal ->
                measurementRepository
                    .observeMeasurements(meal.id, date)
                    .flatMapLatest { measurements ->
                        meal.toUseCaseModel(measurements)
                    }
            }.combine {
                it.toList()
            }
        }

    private fun DomainModel.toUseCaseModel(measurements: List<FoodMeasurement>): Flow<Meal> {
        if (measurements.isEmpty()) {
            return flowOf(
                Meal.empty(
                    id = id,
                    name = name,
                    from = from,
                    fromString = stringFormatRepository.formatTime(from),
                    to = to,
                    toString = stringFormatRepository.formatTime(to),
                    rank = rank
                )
            )
        }

        val productFlows = measurements
            .map { it.foodId }
            .filterIsInstance<FoodId.Product>()
            .map { it.productId }
            .map { productRepository.observeProductById(it).filterNotNull() }

        return productFlows.combine { products ->
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
                fromString = stringFormatRepository.formatTime(from),
                to = to,
                toString = stringFormatRepository.formatTime(to),
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
