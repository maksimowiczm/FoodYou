package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class CreateMealSettingsCardViewModel(
    private val diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository,
    private val coroutineScope: CoroutineScope
) {
    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)

    fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        coroutineScope.launch {
            diaryRepository.createMeal(
                name = name,
                from = from,
                to = to
            )
        }
    }
}
