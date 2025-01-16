package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.database.MealId

enum class Meal {
    Breakfast,
    Lunch,
    Dinner,
    Snacks
}

fun MealId.toDomain(): Meal {
    return when (this) {
        MealId.BreakfastId -> Meal.Breakfast
        MealId.LunchId -> Meal.Lunch
        MealId.DinnerId -> Meal.Dinner
        MealId.SnackId -> Meal.Snacks
        else -> throw IllegalArgumentException("Unknown meal id: $this")
    }
}
