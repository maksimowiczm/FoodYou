package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime

class MealSettingsCardViewModel(
    private val diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository,
    mealId: Long,
    private val coroutineScope: CoroutineScope
) {
    private val isLoading = MutableStateFlow(false)

    private val meal = diaryRepository.observeMealById(mealId).stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = runBlocking {
            diaryRepository.observeMealById(mealId).filterNotNull().first()
        }
    )

    private val flow: Flow<MealCardState> = combine(
        isLoading,
        meal
    ) { isLoading, meal ->
        MealCardState(
            isLoading = isLoading,
            meal = meal
        )
    }

    val state: StateFlow<MealCardState> = flow.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = runBlocking { flow.first() }
    )

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)

    fun updateMeal(meal: Meal) {
        coroutineScope.launch {
            isLoading.value = true
            diaryRepository.updateMeal(meal)
            isLoading.value = false
        }
    }

    fun deleteMeal(meal: Meal) {
        coroutineScope.launch {
            isLoading.value = true
            diaryRepository.deleteMeal(meal)
            isLoading.value = false
        }
    }
}

data class MealCardState(val isLoading: Boolean, val meal: Meal)
