package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences

data class MealsPreferences(
    val layout: MealsCardsLayout,
    val useTimeBasedSorting: Boolean,
    val ignoreAllDayMeals: Boolean,
) : UserPreferences
