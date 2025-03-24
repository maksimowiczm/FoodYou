package com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen

import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.datetime.LocalDate

class CaloriesScreenViewModel(
    diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository
) : DiaryViewModel(
    diaryRepository
) {
    fun formatDate(date: LocalDate) = stringFormatRepository.formatDate(date)
}
