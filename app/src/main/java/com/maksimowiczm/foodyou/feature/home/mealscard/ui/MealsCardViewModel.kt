package com.maksimowiczm.foodyou.feature.home.mealscard.ui

import com.maksimowiczm.foodyou.data.DateProvider
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import kotlinx.datetime.LocalTime

class MealsCardViewModel(
    diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository,
    dateProvider: DateProvider
) : DiaryViewModel(
    diaryRepository
) {
    val time = dateProvider.observeMinutes()

    fun formatTime(time: LocalTime): String = stringFormatRepository.formatTime(time)
}
