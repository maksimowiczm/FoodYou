package com.maksimowiczm.foodyou.feature.goals.ui.screen

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.feature.goals.data.GoalsRepository
import kotlinx.datetime.LocalDate

internal class CaloriesScreenViewModel(
    private val repository: GoalsRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = repository.observeDiaryDay(date)

    fun formatDate(date: LocalDate) = dateFormatter.formatDate(date)
}
