package com.maksimowiczm.foodyou.feature.goals.master

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.goals.domain.entity.DailyGoal

@Immutable internal data class GoalsScreenUiState(val meals: List<MealModel>, val goal: DailyGoal)
