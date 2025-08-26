package com.maksimowiczm.foodyou.feature.home.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDailyGoalsQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryMealsQuery
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFactsField
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.sum
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

internal class GoalsViewModel(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val commandBus: CommandBus,
    private val queryBus: QueryBus,
) : ViewModel() {

    private val dateState = MutableStateFlow<LocalDate?>(null)

    fun setDate(date: LocalDate) {
        dateState.value = date
    }

    private val _expandGoalsCard = observeSettingsUseCase.observe().map { it.expandGoalCard }
    val expandGoalsCard: StateFlow<Boolean> =
        _expandGoalsCard.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _expandGoalsCard.first() },
        )

    fun setExpandGoalsCard(expand: Boolean) {
        viewModelScope.launch {
            commandBus.dispatch(PartialSettingsUpdateCommand(expandGoalCard = expand))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val model: StateFlow<DaySummaryModel?> =
        dateState
            .filterNotNull()
            .flatMapLatest { date ->
                combine(
                    queryBus.dispatch(ObserveDiaryMealsQuery(date)),
                    queryBus.dispatch(ObserveDailyGoalsQuery(date)),
                ) { meals, goal ->
                    val facts = meals.map { it.nutritionFacts }.sum()

                    DaySummaryModel(
                        energy = facts.energy.value?.roundToInt() ?: 0,
                        energyGoal = goal[NutritionFactsField.Energy].roundToInt(),
                        proteins = facts.proteins.value?.roundToInt() ?: 0,
                        proteinsGoal = goal[NutritionFactsField.Proteins].roundToInt(),
                        carbohydrates = facts.carbohydrates.value?.roundToInt() ?: 0,
                        carbohydratesGoal = goal[NutritionFactsField.Carbohydrates].roundToInt(),
                        fats = facts.fats.value?.roundToInt() ?: 0,
                        fatsGoal = goal[NutritionFactsField.Fats].roundToInt(),
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )
}
