package com.maksimowiczm.foodyou.feature.diary.ui.meal

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryDayMealViewModel(
    diaryRepository: DiaryRepository,
    private val addFoodRepository: AddFoodRepository,
    private val stringFormatRepository: StringFormatRepository
) : DiaryViewModel(diaryRepository) {

    fun formatTime(time: LocalTime): String = stringFormatRepository.formatTime(time)
    fun formatDate(date: LocalDate): String = stringFormatRepository.formatDate(date)

    private val deleteChannel = Channel<Long>()
    val deleteEvent = deleteChannel.receiveAsFlow()

    fun onDeleteEntry(entryId: Long) {
        viewModelScope.launch {
            addFoodRepository.removeMeasurement(entryId)
            deleteChannel.send(entryId)
        }
    }

    fun onDeleteEntryUndo(entryId: Long) {
        viewModelScope.launch {
            addFoodRepository.restoreMeasurement(entryId)
        }
    }
}
