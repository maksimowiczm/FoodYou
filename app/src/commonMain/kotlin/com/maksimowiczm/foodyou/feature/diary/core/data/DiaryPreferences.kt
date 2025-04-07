package com.maksimowiczm.foodyou.feature.diary.core.data

import androidx.datastore.preferences.core.booleanPreferencesKey

object DiaryPreferences {
    val timeBasedSorting = booleanPreferencesKey("time_based_sorting")
    val includeAllDayMeals = booleanPreferencesKey("include_all_day_meals")
}
