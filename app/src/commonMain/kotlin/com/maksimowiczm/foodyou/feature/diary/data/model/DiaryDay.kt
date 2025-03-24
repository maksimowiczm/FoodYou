package com.maksimowiczm.foodyou.feature.diary.data.model

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
    fun totalCalories(meal: Meal) = mealProductMap[meal]?.sumOf { it.calories } ?: 0f

    fun totalProteins(meal: Meal) = mealProductMap[meal]?.sumOf { it.proteins } ?: 0f

    fun totalCarbohydrates(meal: Meal) = mealProductMap[meal]?.sumOf { it.carbohydrates } ?: 0f

    fun totalFats(meal: Meal) = mealProductMap[meal]?.sumOf { it.fats } ?: 0f

    /**
     * Total calories for the diary day.
     */
    val totalCalories: Float
        get() = mealProductMap.values.flatten().sumOf { it.calories }

    fun totalCalories(meals: List<Meal>) = meals.sumOf { totalCalories(it) }

    val totalProteins: Float
        get() = mealProductMap.values.flatten().sumOf { it.proteins }

    fun totalProteins(meals: List<Meal>) = meals.sumOf { totalProteins(it) }

    val totalCarbohydrates: Float
        get() = mealProductMap.values.flatten().sumOf { it.carbohydrates }

    fun totalCarbohydrates(meals: List<Meal>) = meals.sumOf { totalCarbohydrates(it) }

    val totalFats: Float
        get() = mealProductMap.values.flatten().sumOf { it.fats }

    fun totalFats(meals: List<Meal>) = meals.sumOf { totalFats(it) }

    /**
     * Total nutrient for the given meals in the diary day.
     */
    fun total(nutrient: Nutrient, meals: List<Meal>): NutrientSummary {
        val products = meals.flatMap { mealProductMap[it] ?: emptyList() }

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
    selector(element) + acc
}
