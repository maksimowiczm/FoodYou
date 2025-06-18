package com.maksimowiczm.foodyou.feature.diary.goals.ui.screen

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.goals.domain.GoalsRepository
import kotlinx.datetime.LocalDate

internal class CaloriesScreenViewModel(private val repository: GoalsRepository) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = repository.observeDiaryDay(date)
}
