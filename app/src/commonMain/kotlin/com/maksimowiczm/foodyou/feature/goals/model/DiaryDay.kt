package com.maksimowiczm.foodyou.feature.goals.model

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.model.DailyGoals
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate
import pro.respawn.kmmutils.common.sumOf

@Immutable
internal data class DiaryDay(
    val date: LocalDate,
    val foods: Map<Meal, List<Food>>,
    val dailyGoals: DailyGoals
) {
    val totalCalories: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.nutrients.calories.value / 100f
            }
        }.roundToInt()

    val totalProteins: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.nutrients.proteins.value / 100f
            }
        }.roundToInt()

    val totalCarbohydrates: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.nutrients.carbohydrates.value / 100f
            }
        }.roundToInt()

    val totalFats: Int
        get() = foods.values.sumOf {
            it.sumOf {
                val weight = it.weight ?: return@sumOf 0f
                weight * it.nutrients.fats.value / 100f
            }
        }.roundToInt()

    val meals = foods.keys.toList()
}
