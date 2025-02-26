package com.maksimowiczm.foodyou.feature.home.mealscard.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.data.AddFoodRepository
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.home.mealscard.MealsCard
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryDayMealViewModel(
    diaryRepository: DiaryRepository,
    private val addFoodRepository: AddFoodRepository,
    private val stringFormatRepository: StringFormatRepository,
    savedStateHandle: SavedStateHandle
) : DiaryViewModel(diaryRepository) {
    val mealId = savedStateHandle.toRoute<MealsCard.Meal>().mealId
    val date = LocalDate.fromEpochDays(savedStateHandle.toRoute<MealsCard.Meal>().epochDay)

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
