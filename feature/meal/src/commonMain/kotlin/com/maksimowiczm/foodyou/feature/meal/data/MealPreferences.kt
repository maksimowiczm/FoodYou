package com.maksimowiczm.foodyou.feature.meal.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object MealPreferences {
    val timeBasedSorting = booleanPreferencesKey("time_based_sorting")
    val ignoreAllDayMeals = booleanPreferencesKey("ignore_all_day_meals")
    val useHorizontalLayout = booleanPreferencesKey("use_horizontal_layout")
}
