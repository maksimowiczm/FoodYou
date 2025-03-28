package com.maksimowiczm.foodyou.feature.diary.ui.meal.model

data class Meal(
    val id: Long,
    val name: String,
    val date: String,
    val from: String,
    val to: String,
    val isAllDay: Boolean,
    val foods: List<MealFoodListItem>
) {
    val isEmpty: Boolean
        get() = foods.isEmpty()

    val calories
        get() = foods.sumOf { it.calories }
    val proteins
        get() = foods.sumOf { it.proteins }
    val carbohydrates
        get() = foods.sumOf { it.carbohydrates }
    val fats
        get() = foods.sumOf { it.fats }
}
