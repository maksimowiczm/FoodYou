package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.MealId

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

fun Meal.toEntity(): MealId {
    return when (this) {
        Meal.Breakfast -> MealId.BreakfastId
        Meal.Lunch -> MealId.LunchId
        Meal.Dinner -> MealId.DinnerId
        Meal.Snacks -> MealId.SnackId
    }
}
