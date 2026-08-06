package com.maksimowiczm.foodyou.features.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealScheduleViewModel(private val mealPlanService: MealPlanService) : ViewModel() {
    private val eventBus = Channel<MealScheduleEvent>()
    val events = eventBus.receiveAsFlow()

    val meals =
        mealPlanService
            .observe()
            .map { it.meals }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    fun update(newMeals: List<Meal>) {
        viewModelScope.launch {
            mealPlanService.transact { it.update(newMeals) }
            eventBus.send(MealScheduleEvent.Updated)
        }
    }
}
