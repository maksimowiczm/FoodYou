package com.maksimowiczm.foodyou.feature.diary.addfood.meal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.diary.addfood.meal.domain.ObserveMealUseCase
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal class MealScreenViewModel(
    observeMealUseCase: ObserveMealUseCase,
    private val stringFormatRepository: StringFormatRepository,
    private val measurementRepository: MeasurementRepository,
    mealId: Long,
    date: LocalDate
) : ViewModel() {
    private val deletedMeasurementEvent = Channel<MeasurementId>()
    val deletedMeasurement = deletedMeasurementEvent.receiveAsFlow()

    val meal = observeMealUseCase(
        mealId = mealId,
        date = date
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    fun formatDate(date: LocalDate) = stringFormatRepository.formatDate(date)

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)

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
