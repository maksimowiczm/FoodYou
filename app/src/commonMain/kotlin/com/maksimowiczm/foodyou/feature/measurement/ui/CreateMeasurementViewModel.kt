package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

    fun onDeleteMeasurement() = launch {
        foodRepository.deleteFood(id = foodId)
        eventBus.send(MeasurementScreenEvent.Deleted)
    }
}
