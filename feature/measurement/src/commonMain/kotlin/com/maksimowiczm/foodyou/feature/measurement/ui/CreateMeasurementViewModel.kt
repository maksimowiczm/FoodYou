package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.domain.FoodRepository
import com.maksimowiczm.foodyou.core.domain.MealRepository
import com.maksimowiczm.foodyou.core.domain.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class CreateMeasurementViewModel(
    private val foodId: FoodId,
    private val foodRepository: FoodRepository,
    mealsRepository: MealRepository,
    private val measurementRepository: MeasurementRepository
) : ViewModel() {
    val food = foodRepository.observeFood(foodId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val meals = mealsRepository.observeMeals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val suggestions = measurementRepository.observeSuggestions(foodId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val eventBus = Channel<MeasurementScreenEvent>()
    val measurementCreatedEventBus = eventBus.receiveAsFlow()

    fun onCreateMeasurement(date: LocalDate, mealId: Long, measurement: Measurement) = launch {
        measurementRepository.addMeasurement(
            date = date,
            mealId = mealId,
            foodId = foodId,
            measurement = measurement
        )
        eventBus.send(MeasurementScreenEvent.Done)
    }

    fun onExplodeRecipe(date: LocalDate, mealId: Long, measurement: Measurement) = try {
        val recipe = food.value as? Recipe

        checkNotNull(recipe) {
            "Food with id $foodId is null or not a recipe"
        }

        val weight = when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> measurement.quantity * recipe.totalWeight
            is Measurement.Serving -> measurement.quantity * recipe.servingWeight
        }

        val fractions = recipe.ingredientFractions()
        val measurements = recipe.ingredients.map { ingredient ->
            val fraction = fractions[ingredient.food.id]

            checkNotNull(fraction) {
                "Fraction for ingredient ${ingredient.food} in recipe $recipe is null"
            }

            val ingredientWeight = weight * fraction

            val measurement = when (ingredient.measurement) {
                is Measurement.Gram -> Measurement.Gram(ingredientWeight)

                is Measurement.Package -> {
                    val packageWeight = ingredient.food.totalWeight
                    checkNotNull(packageWeight) {
                        "No total weight for ingredient: ${ingredient.food}"
                    }

                    val quantity = ingredientWeight / packageWeight
                    Measurement.Package(quantity)
                }

                is Measurement.Serving -> {
                    val servingWeight = ingredient.food.servingWeight

                    checkNotNull(servingWeight) {
                        "No serving weight for ingredient: ${ingredient.food}"
                    }

                    val quantity = ingredientWeight / servingWeight
                    Measurement.Serving(quantity)
                }
            }

            ingredient to measurement
        }

        val addJobs = measurements.map { (ingredient, measurement) ->
            viewModelScope.async {
                measurementRepository.addMeasurement(
                    date = date,
                    mealId = mealId,
                    foodId = ingredient.food.id,
                    measurement = measurement
                )
            }
        }

        viewModelScope.launch {
            awaitAll(*addJobs.toTypedArray())
            eventBus.send(MeasurementScreenEvent.Done)
        }

        Unit
    } catch (it: Throwable) {
        Logger.e(TAG) {
            "Failed to explode recipe with measurement: $measurement. Error: ${it.message}"
        }
    }

    fun onDeleteMeasurement() = launch {
        foodRepository.deleteFood(id = foodId)
        eventBus.send(MeasurementScreenEvent.Deleted)
    }

    private companion object {
        private const val TAG = "CreateMeasurementViewModel"
    }
}
