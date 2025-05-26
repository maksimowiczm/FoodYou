package com.maksimowiczm.foodyou.feature.goals.ui.card

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.goals.data.GoalsRepository
import kotlinx.datetime.LocalDate

internal class GoalsCardViewModel(private val repository: GoalsRepository) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = repository.observeDiaryDay(date)
}
