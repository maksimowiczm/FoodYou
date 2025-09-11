package com.maksimowiczm.foodyou.app.business.opensource.domain.fooddiary

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences

data class MealsPreferences(
    val layout: MealsCardsLayout,
    val useTimeBasedSorting: Boolean,
    val ignoreAllDayMeals: Boolean,
) : UserPreferences
