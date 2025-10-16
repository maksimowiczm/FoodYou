package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.Language

interface FoodNameSelector {

    /** Selects the most appropriate food name based on the user's locale preferences. */
    fun select(foodName: FoodName): String

    /** Selects the most appropriate food name language based on the user's locale preferences. */
    fun select(): Language
}
