package com.maksimowiczm.foodyou.feature.diary.ui.meal.model

data class Meal(
    val id: Long,
    val name: String,
    val date: String,
    val from: String,
    val to: String,
    val isAllDay: Boolean,
    val foods: List<MealFoodListItem>,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int
) {
    val isEmpty: Boolean
        get() = foods.isEmpty()
}
