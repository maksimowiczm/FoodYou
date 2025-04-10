package com.maksimowiczm.foodyou.feature.goals.ui.screen

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.feature.goals.data.DiaryDayRepository
import kotlinx.datetime.LocalDate

internal class CaloriesScreenViewModel(
    private val diaryDayRepository: DiaryDayRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = diaryDayRepository.observeDiaryDay(date)

    fun formatDate(date: LocalDate) = dateFormatter.formatDate(date)
}
