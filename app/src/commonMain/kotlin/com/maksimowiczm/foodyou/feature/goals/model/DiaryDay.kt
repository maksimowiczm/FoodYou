package com.maksimowiczm.foodyou.feature.goals.model

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.domain.model.DailyGoals
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.ext.sumOf
import com.maksimowiczm.foodyou.core.model.Meal
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate

@Immutable
internal data class DiaryDay(
    val date: LocalDate,
    val foods: Map<Meal, List<FoodWithMeasurement>>,
    val dailyGoals: DailyGoals
) {
    val totalCalories: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.food.nutritionFacts.calories.value / 100f
            }
        }.roundToInt()

    val totalProteins: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.food.nutritionFacts.proteins.value / 100f
            }
        }.roundToInt()

    val totalCarbohydrates: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.food.nutritionFacts.carbohydrates.value / 100f
            }
        }.roundToInt()

    val totalFats: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.food.nutritionFacts.fats.value / 100f
            }
        }.roundToInt()

    private val meals = foods.keys.toList()

    val nonEmptyMeals: List<Meal>
        get() = meals.filter { foods[it].orEmpty().isNotEmpty() }
}
