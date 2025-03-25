package com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen

import com.maksimowiczm.foodyou.feature.diary.domain.ObserveDiaryDayUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.datetime.LocalDate

class CaloriesScreenViewModel(
    observeDiaryDayUseCase: ObserveDiaryDayUseCase,
    private val stringFormatRepository: StringFormatRepository
) : DiaryViewModel(observeDiaryDayUseCase) {
    fun formatDate(date: LocalDate) = stringFormatRepository.formatDate(date)
}
