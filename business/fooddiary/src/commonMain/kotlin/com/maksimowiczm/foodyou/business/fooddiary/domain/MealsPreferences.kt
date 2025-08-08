package com.maksimowiczm.foodyou.business.fooddiary.domain

data class MealsPreferences(
    val layout: MealsCardsLayout,
    val useTimeBasedSorting: Boolean,
    val ignoreAllDayMeals: Boolean,
)
