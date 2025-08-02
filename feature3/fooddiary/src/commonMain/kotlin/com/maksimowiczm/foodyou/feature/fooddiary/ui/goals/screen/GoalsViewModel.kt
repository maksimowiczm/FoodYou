package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveDailyGoalUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import kotlinx.datetime.LocalDate

internal class GoalsViewModel(
    private val observeMealsUseCase: ObserveMealsUseCase,
    private val observeDailyGoalUseCase: ObserveDailyGoalUseCase
) : ViewModel() {

    fun observeMeals(date: LocalDate) = observeMealsUseCase(date)

    fun observeGoals(date: LocalDate) = observeDailyGoalUseCase.observe(date)
}
