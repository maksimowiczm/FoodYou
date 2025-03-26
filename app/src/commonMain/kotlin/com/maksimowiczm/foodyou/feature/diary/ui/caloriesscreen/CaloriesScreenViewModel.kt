package com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen

import com.maksimowiczm.foodyou.feature.diary.domain.ObserveDiaryDayUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryDayViewModel
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.datetime.LocalDate

class CaloriesScreenViewModel(
    observeDiaryDayUseCase: ObserveDiaryDayUseCase,
    private val stringFormatRepository: StringFormatRepository
) : DiaryDayViewModel(observeDiaryDayUseCase) {
    fun formatDate(date: LocalDate) = stringFormatRepository.formatDate(date)
}
