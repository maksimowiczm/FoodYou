package com.maksimowiczm.foodyou.app.ui.diary.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FoodDiarySearchViewModel(
    private val mealIdentity: MealIdentity,
    mealPlanService: MealPlanService,
) : ViewModel() {
    val meal =
        mealPlanService
            .observe()
            .map {
                it.meals.find { it.identity == mealIdentity }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )
}
