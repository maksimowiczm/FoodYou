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

    fun unpackRecipe(date: LocalDate, mealId: Long, measurement: Measurement) {
        if (foodId !is FoodId.Recipe) {
            Logger.e(TAG) { "Unpacking recipe failed: Food ID is not a Recipe ID." }
            return
        }

        viewModelScope.launch {
            measurementRepository.unpackRecipe(
                date = date,
                mealId = mealId,
                recipeId = foodId,
                measurement = measurement
            )

            eventBus.send(MeasurementScreenEvent.Done)
        }
    }

    fun onDeleteMeasurement() = launch {
        foodRepository.deleteFood(id = foodId)
        eventBus.send(MeasurementScreenEvent.FoodDeleted)
    }

    private companion object {
        const val TAG = "CreateMeasurementViewModel"
    }
}
