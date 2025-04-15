package com.maksimowiczm.foodyou.feature.meal.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object MealPreferences {
    val timeBasedSorting = booleanPreferencesKey("time_based_sorting")
    val includeAllDayMeals = booleanPreferencesKey("include_all_day_meals")

    val useVerticalLayout = booleanPreferencesKey("use_vertical_layout")
    val useCompactLayout = booleanPreferencesKey("use_compact_layout")
}
