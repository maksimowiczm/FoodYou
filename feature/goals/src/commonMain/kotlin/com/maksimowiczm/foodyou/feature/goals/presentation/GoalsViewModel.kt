package com.maksimowiczm.foodyou.feature.goals.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDailyGoalsQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryMealsQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryMeal
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.isComplete
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal class GoalsViewModel(private val queryBus: QueryBus) : ViewModel() {
    private val mealsFlows = mutableMapOf<LocalDate, StateFlow<GoalsScreenUiState?>>()

    fun observeUiStateByDate(date: LocalDate): StateFlow<GoalsScreenUiState?> {
        mealsFlows[date]?.let {
            return it
        }

        val meals =
            queryBus.dispatch<List<DiaryMeal>>(ObserveDiaryMealsQuery(date)).map { list ->
                list.map {
                    MealModel(
                        id = it.meal.id,
                        name = it.meal.name,
                        nutritionFacts = it.nutritionFacts,
                        incompleteFoods =
                            it.entries
                                .filterNot { it.nutritionFacts.isComplete }
                                .map { it.food.name }
                                .distinct(),
                    )
                }
            }
        val goal = queryBus.dispatch<DailyGoal>(ObserveDailyGoalsQuery(date))

        val flow =
            combine(meals, goal, ::GoalsScreenUiState)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(30_000),
                    initialValue = null,
                )

        mealsFlows[date] = flow
        return flow
    }
}
