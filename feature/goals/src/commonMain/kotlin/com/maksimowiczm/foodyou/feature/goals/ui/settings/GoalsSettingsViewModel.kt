package com.maksimowiczm.foodyou.feature.goals.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.goals.domain.GoalsRepository
import com.maksimowiczm.foodyou.feature.goals.model.DailyGoals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class GoalsSettingsViewModel(private val goalsRepository: GoalsRepository) : ViewModel() {

    val goals = goalsRepository.observeDailyGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = runBlocking { goalsRepository.observeDailyGoals().first() }
    )

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    fun onSave(goals: DailyGoals) {
        viewModelScope.launch {
            goalsRepository.setDailyGoals(goals)
            _saved.emit(true)
        }
    }
}
