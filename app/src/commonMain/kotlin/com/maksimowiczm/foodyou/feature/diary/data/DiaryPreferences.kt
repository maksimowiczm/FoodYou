package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object DiaryPreferences {
    val timeBasedSorting = booleanPreferencesKey("time_based_sorting")
    val includeAllDayMeals = booleanPreferencesKey("include_all_day_meals")
}
