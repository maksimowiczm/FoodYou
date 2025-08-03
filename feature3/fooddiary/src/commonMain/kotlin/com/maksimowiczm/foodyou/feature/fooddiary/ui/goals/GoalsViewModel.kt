package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveDailyGoalUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal class GoalsViewModel(
    private val observeMealsUseCase: ObserveMealsUseCase,
    private val observeDailyGoalUseCase: ObserveDailyGoalUseCase
) : ViewModel() {

    private val mealsFlows = mutableMapOf<LocalDate, StateFlow<List<Meal>?>>()
    fun observeMeals(date: LocalDate) = mealsFlows.getOrPut(date) {
        observeMealsUseCase.observe(date).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
    }

    private val dailyGoalFlow = mutableMapOf<LocalDate, StateFlow<DailyGoal?>>()
    fun observeGoals(date: LocalDate) = dailyGoalFlow.getOrPut(date) {
        observeDailyGoalUseCase.observe(date).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
    }
}
