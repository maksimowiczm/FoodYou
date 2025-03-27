package com.maksimowiczm.foodyou.feature.garbage.ui.caloriesscreen

import com.maksimowiczm.foodyou.feature.garbage.domain.ObserveDiaryDayUseCase
import com.maksimowiczm.foodyou.feature.garbage.ui.DiaryDayViewModel
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.datetime.LocalDate

class CaloriesScreenViewModel(
    observeDiaryDayUseCase: ObserveDiaryDayUseCase,
    private val stringFormatRepository: StringFormatRepository
) : DiaryDayViewModel(observeDiaryDayUseCase) {
    fun formatDate(date: LocalDate) = stringFormatRepository.formatDate(date)
}
