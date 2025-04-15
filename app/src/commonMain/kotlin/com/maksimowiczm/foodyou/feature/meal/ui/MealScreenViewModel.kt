package com.maksimowiczm.foodyou.feature.meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealWithFoodUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal class MealScreenViewModel(
    observeMealWithFoodUseCase: ObserveMealWithFoodUseCase,
    private val dateFormatter: DateFormatter,
    private val measurementRepository: MeasurementRepository,
    mealId: Long,
    date: LocalDate
) : ViewModel() {
    private val deletedMeasurementEvent = Channel<MeasurementId>()
    val deletedMeasurement = deletedMeasurementEvent.receiveAsFlow()

    val meal = observeMealWithFoodUseCase(
        mealId = mealId,
        date = date
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    fun formatDate(date: LocalDate) = dateFormatter.formatDate(date)

    fun formatTime(time: LocalTime) = dateFormatter.formatTime(time)

    fun onDeleteMeasurement(measurementId: MeasurementId) {
        viewModelScope.launch {
            measurementRepository.removeMeasurement(measurementId)
            deletedMeasurementEvent.send(measurementId)
        }
    }

    fun onRestoreMeasurement(measurementId: MeasurementId) {
        viewModelScope.launch {
            measurementRepository.restoreMeasurement(measurementId)
        }
    }
}
