package com.maksimowiczm.foodyou.feature.addfood.ui.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.repository.FoodRepository
import com.maksimowiczm.foodyou.core.repository.MeasurementRepository
import com.maksimowiczm.foodyou.feature.addfood.domain.ObserveMeasurableFoodUseCase
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
import kotlinx.datetime.LocalDate

internal class CreateMeasurementScreenViewModel(
    private val date: LocalDate,
    private val mealId: Long,
    private val foodId: FoodId,
    observeMeasurableFoodUseCase: ObserveMeasurableFoodUseCase,
    private val measurementRepository: MeasurementRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {
    val food = observeMeasurableFoodUseCase(foodId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _selectedMeasurement = MutableStateFlow<Measurement?>(null)
    val selectedMeasurement = _selectedMeasurement.asStateFlow()

    private val _eventBus = MutableSharedFlow<MeasurementScreenEvent>()
    val eventBus = _eventBus.asSharedFlow()

    fun onConfirm(measurement: Measurement) {
        viewModelScope.launch {
            if (_selectedMeasurement.value != null) {
                Logger.w(TAG) {
                    "Attempted to create a measurement when one is already being created"
                }
                return@launch
            }

            _selectedMeasurement.emit(measurement)

            // Make it no faster than 100ms
            awaitAll(
                async {
                    measurementRepository.addMeasurement(
                        date = date,
                        mealId = mealId,
                        foodId = foodId,
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

    private companion object {
        const val TAG = "CreateMeasurementScreenViewModel"
    }
}
