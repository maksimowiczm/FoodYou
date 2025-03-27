package com.maksimowiczm.foodyou.feature.diary.ui.mealscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealByDateUseCase
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class MealScreenViewModel(
    private val observeMealByDate: ObserveMealByDateUseCase,
    private val stringFormatRepository: StringFormatRepository
) : ViewModel() {
    fun observeMeal(date: LocalDate, mealId: Long) =
        observeMealByDate.observeMealByDateUseCase(date, mealId)

    fun formatTime(time: LocalTime): String = stringFormatRepository.formatTime(time)
    fun formatDate(date: LocalDate): String = stringFormatRepository.formatDate(date)

    private val deleteChannel = Channel<Long>()
    val deleteEvent = deleteChannel.receiveAsFlow()

    fun onDeleteEntry(entryId: Long) {
        viewModelScope.launch {
            deleteChannel.send(entryId)
        }
    }

    fun onDeleteEntryUndo(entryId: Long) {
        viewModelScope.launch {
        }
    }
}
