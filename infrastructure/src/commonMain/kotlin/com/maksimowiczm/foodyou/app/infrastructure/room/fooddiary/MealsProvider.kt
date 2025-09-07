package com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary

fun interface MealsProvider {
    fun getMeals(): List<MealEntity>
}
