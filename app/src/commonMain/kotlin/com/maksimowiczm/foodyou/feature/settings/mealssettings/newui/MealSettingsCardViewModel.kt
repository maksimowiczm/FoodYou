package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
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
    val meal = diaryRepository.observeMealById(mealId).filterNotNull().stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = runBlocking {
            diaryRepository.observeMealById(mealId).filterNotNull().first()
        }
    )

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)

    fun updateMeal(meal: Meal) {
        coroutineScope.launch {
            diaryRepository.updateMeal(meal)
        }
    }

    fun deleteMeal(meal: Meal) {
        coroutineScope.launch {
            diaryRepository.deleteMeal(meal)
        }
    }
}
