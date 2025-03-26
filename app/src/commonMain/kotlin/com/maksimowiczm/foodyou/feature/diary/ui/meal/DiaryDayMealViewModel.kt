package com.maksimowiczm.foodyou.feature.diary.ui.meal

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveDiaryDayUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryDayViewModel
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryDayMealViewModel(
    observeDiaryDayUseCase: ObserveDiaryDayUseCase,
    private val measurementRepository: MeasurementRepository,
    private val stringFormatRepository: StringFormatRepository
) : DiaryDayViewModel(observeDiaryDayUseCase) {

    fun formatTime(time: LocalTime): String = stringFormatRepository.formatTime(time)
    fun formatDate(date: LocalDate): String = stringFormatRepository.formatDate(date)

    private val deleteChannel = Channel<Long>()
    val deleteEvent = deleteChannel.receiveAsFlow()

    fun onDeleteEntry(entryId: Long) {
        viewModelScope.launch {
            measurementRepository.removeMeasurement(entryId)
            deleteChannel.send(entryId)
        }
    }

    fun onDeleteEntryUndo(entryId: Long) {
        viewModelScope.launch {
            measurementRepository.restoreMeasurement(entryId)
        }
    }
}
