package com.maksimowiczm.foodyou.feature.diary.ui.mealscard

import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.feature.system.data.DateProvider
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
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
