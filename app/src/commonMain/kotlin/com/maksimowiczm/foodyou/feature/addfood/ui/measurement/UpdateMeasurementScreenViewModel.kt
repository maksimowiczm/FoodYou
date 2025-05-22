package com.maksimowiczm.foodyou.feature.addfood.ui.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.feature.measurement.ObserveMeasurableFoodUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateMeasurementScreenViewModel(
    private val measurementId: MeasurementId,
    observeMeasurableFoodUseCase: ObserveMeasurableFoodUseCase,
    private val measurementRepository: MeasurementRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {
    val food = observeMeasurableFoodUseCase(measurementId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _selectedMeasurement = MutableStateFlow<Measurement?>(null)
    val selectedMeasurement = _selectedMeasurement.asStateFlow()

    private val _eventBus = MutableSharedFlow<MeasurementScreenEvent>()
    val eventBus = _eventBus.asSharedFlow()

    fun onConfirm(measurement: Measurement) {
        viewModelScope.launch {
            if (_selectedMeasurement.value != null) {
                Logger.Companion.w(TAG) {
                    "Attempted to create a measurement when one is already being created"
                }
                return@launch
            }

            _selectedMeasurement.emit(measurement)

            // Make it no faster than 100ms
            awaitAll(
                async {
                    measurementRepository.updateMeasurement(
                        measurementId = measurementId,
                        measurement = measurement
                    )
                },
                async {
                    delay(100)
                }
            )

            _eventBus.emit(MeasurementScreenEvent.Closed)
        }
    }

    fun onDeleteFood(foodId: FoodId) {
        viewModelScope.launch {
            foodRepository.deleteFood(foodId)
            _eventBus.emit(MeasurementScreenEvent.FoodDeleted)
        }
    }

    fun onCloneRecipe(recipeId: FoodId.Recipe, suffix: String) = launch {
        val newId = foodRepository.cloneRecipeIntoProduct(recipeId, suffix)
        _eventBus.emit(MeasurementScreenEvent.RecipeClonedIntoProduct(newId))
    }

    private companion object {
        const val TAG = "UpdateMeasurementScreenViewModel"
    }
}
