package com.maksimowiczm.foodyou.feature.diary.ui.goalssettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.GoalsRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class GoalsSettingsViewModel(private val goalsRepository: GoalsRepository) : ViewModel() {
    val dailyGoals = goalsRepository.observeDailyGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2000),
        initialValue = runBlocking { goalsRepository.observeDailyGoals().first() }
    )

    suspend fun onSaveDailyGoals(goals: DailyGoals) {
        goalsRepository.setDailyGoals(goals)
    }
}
