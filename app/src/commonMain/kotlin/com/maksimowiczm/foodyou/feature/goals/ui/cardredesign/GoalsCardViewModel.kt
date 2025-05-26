package com.maksimowiczm.foodyou.feature.goals.ui.cardredesign

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.goals.data.GoalsRepository
import kotlinx.datetime.LocalDate

internal class GoalsCardViewModel(private val repository: GoalsRepository) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = repository.observeDiaryDay(date)
}
