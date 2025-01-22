package com.maksimowiczm.foodyou.feature.addfood.database

@JvmInline
value class MealId(val value: Long) {
    companion object {
        val BreakfastId = MealId(0)
        val LunchId = MealId(1)
        val DinnerId = MealId(2)
        val SnackId = MealId(3)
    }
}
