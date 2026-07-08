package com.maksimowiczm.foodyou.mealplan.domain

import kotlinx.serialization.Serializable

@Serializable
enum class MealType {
    Breakfast,
    SecondBreakfast,
    Lunch,
    AfternoonSnack,
    Dinner,
    Snacks,
}
