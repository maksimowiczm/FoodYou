package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.core.repository.FoodRepository
import com.maksimowiczm.foodyou.core.repository.MeasurementRepository
import com.maksimowiczm.foodyou.feature.measurement.domain.ObserveMeasurableFoodUseCase
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
) : MeasurementScreenViewModel() {
    override val food = observeMeasurableFoodUseCase(measurementId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _selectedMeasurement = MutableStateFlow<Measurement?>(null)
    override val selectedMeasurement = _selectedMeasurement.asStateFlow()

    private val _eventBus = MutableSharedFlow<MeasurementScreenEvent>()
    override val eventBus = _eventBus.asSharedFlow()

    override fun onConfirm(measurement: Measurement) {
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

    override fun onDeleteFood(foodId: FoodId) {
        viewModelScope.launch {
            foodRepository.deleteFood(foodId)

            _eventBus.emit(MeasurementScreenEvent.FoodDeleted)
        }
    }

    private companion object {
        const val TAG = "UpdateMeasurementScreenViewModel"
    }
}
