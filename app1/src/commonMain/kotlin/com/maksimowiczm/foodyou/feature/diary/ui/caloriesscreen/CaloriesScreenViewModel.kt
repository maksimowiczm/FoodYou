package com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen

import com.maksimowiczm.foodyou.feature.diary.data.DiaryDayRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryDayViewModel
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.datetime.LocalDate

class CaloriesScreenViewModel(
    diaryDayRepository: DiaryDayRepository,
    private val stringFormatRepository: StringFormatRepository
) : DiaryDayViewModel(diaryDayRepository) {
    fun formatDate(date: LocalDate) = stringFormatRepository.formatDate(date)
}
