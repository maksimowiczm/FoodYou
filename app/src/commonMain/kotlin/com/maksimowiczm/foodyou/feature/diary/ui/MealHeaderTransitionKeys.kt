package com.maksimowiczm.foodyou.feature.diary.ui

object MealHeaderTransitionKeys {
    data class MealContainer(val mealId: Long, val epochDay: Int)

    data class MealTitle(val mealId: Long, val epochDay: Int)

    data class MealTime(val mealId: Long, val epochDay: Int)

    data class MealNutrients(val mealId: Long, val epochDay: Int)
}
