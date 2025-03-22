package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.data.NutrientsHelper
import kotlinx.datetime.LocalDate

data class DiaryDay(
    val date: LocalDate,

    /**
     * Map of meals with list of products for each meal.
     */
    val mealProductMap: Map<Meal, List<ProductWithWeightMeasurement>>,

    /**
     * Daily goals for the day.
     */
    val dailyGoals: DailyGoals
) {
    /**
     * Total calories for the meal in the diary day.
     */
    fun totalCalories(meal: Meal): Float = mealProductMap[meal]
        ?.sumOf { it.calories } ?: 0f

    fun totalProteins(meal: Meal): Float = mealProductMap[meal]
        ?.sumOf { it.proteins } ?: 0f

    fun totalCarbohydrates(meal: Meal): Float = mealProductMap[meal]
        ?.sumOf { it.carbohydrates } ?: 0f

    fun totalFats(meal: Meal): Float = mealProductMap[meal]
        ?.sumOf { it.fats } ?: 0f

    fun total(nutrient: Nutrient, meal: Meal): NutrientSummary? {
        val products = mealProductMap[meal]

        if (products == null) {
            return null
        }

        val incomplete = products.any { it.product.nutrients.get(nutrient, it.weight) == null }

        val total = products.fold(0f) { acc, product ->
            val value = product.product.nutrients.get(nutrient, product.weight) ?: 0f
            value + acc
        }

        return if (incomplete) {
            NutrientSummary.Incomplete(total)
        } else {
            NutrientSummary.Complete(total)
        }
    }

    val totalCalories: Float
        get() = mealProductMap.values.flatten()
            .sumOf { it.calories }

    val totalCaloriesProteins: Float
        get() = mealProductMap.values.flatten()
            .sumOf { NutrientsHelper.proteinsToCalories(it.proteins) }

    val totalCaloriesCarbohydrates: Float
        get() = mealProductMap.values.flatten()
            .sumOf { NutrientsHelper.carbohydratesToCalories(it.carbohydrates) }

    val totalCaloriesFats: Float
        get() = mealProductMap.values.flatten()
            .sumOf { NutrientsHelper.fatsToCalories(it.fats) }

    val totalProteins: Float
        get() = mealProductMap.values.flatten()
            .sumOf { it.proteins }

    val totalCarbohydrates: Float
        get() = mealProductMap.values.flatten()
            .sumOf { it.carbohydrates }

    val totalFats: Float
        get() = mealProductMap.values.flatten()
            .sumOf { it.fats }

    /**
     * List of all meals in the diary day.
     */
    val meals: List<Meal> get() = mealProductMap.keys.toList()

    sealed interface NutrientSummary {
        val value: Float

        @JvmInline
        value class Complete(override val value: Float) : NutrientSummary

        @JvmInline
        value class Incomplete(override val value: Float) : NutrientSummary
    }
}

private inline fun <T> List<T>.sumOf(selector: (T) -> Float) = fold(0f) { acc, element ->
    selector(element) +
        acc
}
