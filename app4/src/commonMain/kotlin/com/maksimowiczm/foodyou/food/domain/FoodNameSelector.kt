package com.maksimowiczm.foodyou.food.domain

fun interface FoodNameSelector {

    /** Selects the most appropriate food name based on the user's locale preferences. */
    fun select(foodName: FoodName): String
}
