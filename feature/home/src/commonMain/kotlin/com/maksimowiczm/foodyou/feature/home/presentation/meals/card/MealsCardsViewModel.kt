package com.maksimowiczm.foodyou.feature.home.presentation.meals.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteDiaryEntryCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryMealsQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsPreferencesQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryMeal
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

internal class MealsCardsViewModel(queryBus: QueryBus, private val commandBus: CommandBus) :
    ViewModel() {
    private val dateState = MutableStateFlow<LocalDate?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val diaryMeals: StateFlow<List<MealModel>?> =
        dateState
            .filterNotNull()
            .flatMapLatest { date -> queryBus.dispatch(ObserveDiaryMealsQuery(date)) }
            .map { list -> list.map { it.toMealModel() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(60_000),
                initialValue = null,
            )

    private val _layout = queryBus.dispatch(ObserveMealsPreferencesQuery).map { it.layout }
    val layout =
        _layout.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _layout.first() },
        )

    fun setDate(date: LocalDate) {
        viewModelScope.launch { dateState.value = date }
    }

    fun onDeleteEntry(measurementId: Long) {
        viewModelScope.launch {
            commandBus
                .dispatch(DeleteDiaryEntryCommand(measurementId))
                .consume(
                    onFailure = {
                        FoodYouLogger.e(TAG) {
                            "Failed to delete diary entry with ID $measurementId"
                        }
                    }
                )
        }
    }

    private companion object {
        private const val TAG = "MealsCardsViewModel"
    }
}

private fun DiaryMeal.toMealModel(): MealModel =
    MealModel(
        id = meal.id,
        name = meal.name,
        from = meal.from,
        to = meal.to,
        isAllDay = meal.from == meal.to,
        foods = entries.map { it.toMealEntryModel() },
        energy = nutritionFacts.energy.value?.roundToInt() ?: 0,
        proteins = nutritionFacts.proteins.value ?: 0.0,
        carbohydrates = nutritionFacts.carbohydrates.value ?: 0.0,
        fats = nutritionFacts.fats.value ?: 0.0,
    )

private fun DiaryEntry.toMealEntryModel(): MealEntryModel =
    MealEntryModel(
        id = id,
        name = food.name,
        energy = nutritionFacts.energy.value?.roundToInt(),
        proteins = nutritionFacts.proteins.value,
        carbohydrates = nutritionFacts.carbohydrates.value,
        fats = nutritionFacts.fats.value,
        measurement = measurement,
        weight = weight,
        isLiquid = food.isLiquid,
        isRecipe = food is DiaryFoodRecipe,
        totalWeight = food.totalWeight,
        servingWeight = food.servingWeight,
    )
