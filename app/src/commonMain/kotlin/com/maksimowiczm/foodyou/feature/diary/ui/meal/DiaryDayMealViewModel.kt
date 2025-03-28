package com.maksimowiczm.foodyou.feature.diary.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.ui.meal.cases.ObserveMealCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class DiaryDayMealViewModel(
    observeMealCase: ObserveMealCase,
    private val measurementRepository: MeasurementRepository,
    mealId: Long,
    date: LocalDate
) : ViewModel() {
    val meal = observeMealCase(mealId, date)

    private val deleteChannel = Channel<MeasurementId>()
    val deleteEvent = deleteChannel.receiveAsFlow()

    fun onDeleteEntry(id: MeasurementId) {
        viewModelScope.launch {
            measurementRepository.removeMeasurement(id)
            deleteChannel.send(id)
        }
    }

    fun onDeleteEntryUndo(id: MeasurementId) {
        viewModelScope.launch {
            measurementRepository.restoreMeasurement(id)
        }
    }
}
