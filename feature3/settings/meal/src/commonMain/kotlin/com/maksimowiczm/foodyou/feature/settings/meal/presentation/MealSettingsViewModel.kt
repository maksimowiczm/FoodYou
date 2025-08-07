package com.maksimowiczm.foodyou.feature.settings.meal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateMealWithLastRankCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteMealCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.ReorderMealsCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.dispatchIgnoreResult
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class MealSettingsViewModel(queryBus: QueryBus, private val commandBus: CommandBus) :
    ViewModel() {

    val meals: StateFlow<List<MealModel>?> =
        queryBus
            .dispatch<List<Meal>>(ObserveMealsQuery)
            .distinctUntilChanged()
            .map { meals ->
                meals
                    .sortedBy { it.rank }
                    .map { meal ->
                        MealModel(
                            id = meal.id,
                            name = meal.name,
                            from = meal.from,
                            to = meal.to,
                            isAllDay = meal.from == meal.to,
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun deleteMeal(mealModel: MealModel) {
        viewModelScope.launch { commandBus.dispatchIgnoreResult(DeleteMealCommand(mealModel.id)) }
    }

    fun updateMeal(mealModel: MealModel) {
        viewModelScope.launch {
            UpdateMealCommand(
                    id = mealModel.id,
                    name = mealModel.name,
                    from = mealModel.from,
                    to = mealModel.to,
                )
                .let { command -> commandBus.dispatchIgnoreResult(command) }
        }
    }

    fun createMeal(mealModel: MealModel) {
        viewModelScope.launch {
            CreateMealWithLastRankCommand(
                    name = mealModel.name,
                    from = mealModel.from,
                    to = mealModel.to,
                )
                .let { command -> commandBus.dispatchIgnoreResult(command) }
        }
    }

    fun updateMealOrder(mealModels: List<MealModel>) {
        viewModelScope.launch {
            ReorderMealsCommand(mealModels.map { it.id }).let { command ->
                commandBus.dispatchIgnoreResult(command)
            }
        }
    }
}
