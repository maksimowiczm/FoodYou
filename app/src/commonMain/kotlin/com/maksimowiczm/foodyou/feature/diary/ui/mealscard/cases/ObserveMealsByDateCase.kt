package com.maksimowiczm.foodyou.feature.diary.ui.mealscard.cases

import com.maksimowiczm.foodyou.ext.combine
import com.maksimowiczm.foodyou.ext.sumOf
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.RecipeRepository
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
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveMealsByDateCase(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    private val productRepository: ProductRepository,
    private val stringFormatRepository: StringFormatRepository,
    private val recipeRepository: RecipeRepository
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

    private data class MealItem(
        val calories: Float,
        val proteins: Float,
        val carbohydrates: Float,
        val fats: Float
    )

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

        return measurements
            .map { measurement ->
                when (measurement.foodId) {
                    is FoodId.Product ->
                        productRepository
                            .observeProductById(measurement.foodId.productId)
                            .filterNotNull()
                            .map {
                                val weight = measurement.measurement.getWeight(it)
                                MealItem(
                                    calories = it.nutrients.calories * weight / 100,
                                    proteins = it.nutrients.proteins * weight / 100,
                                    carbohydrates = it.nutrients.carbohydrates * weight / 100,
                                    fats = it.nutrients.fats * weight / 100
                                )
                            }

                    is FoodId.Recipe ->
                        recipeRepository
                            .observeRecipeById(measurement.foodId.recipeId)
                            .filterNotNull()
                            .map { recipe ->
                                MealItem(
                                    calories = recipe.nutrients.calories,
                                    proteins = recipe.nutrients.proteins,
                                    carbohydrates = recipe.nutrients.carbohydrates,
                                    fats = recipe.nutrients.fats
                                )
                            }
                }
            }.combine {
                val calories = it.sumOf { it.calories }.roundToInt()
                val proteins = it.sumOf { it.proteins }.roundToInt()
                val carbohydrates = it.sumOf { it.carbohydrates }.roundToInt()
                val fats = it.sumOf { it.fats }.roundToInt()

                Meal(
                    id = id,
                    name = name,
                    from = from,
                    fromString = stringFormatRepository.formatTime(from),
                    to = to,
                    toString = stringFormatRepository.formatTime(to),
                    rank = rank,
                    calories = calories,
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    isEmpty = false
                )
            }
    }
}
