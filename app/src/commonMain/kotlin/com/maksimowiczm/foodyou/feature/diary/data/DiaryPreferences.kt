package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object DiaryPreferences {
    val caloriesGoal = intPreferencesKey("calories_goal")
    val proteinsGoal = floatPreferencesKey("proteins_goal")
    val carbohydratesGoal = floatPreferencesKey("carbohydrates_goal")
    val fatsGoal = floatPreferencesKey("fats_goal")

    val timeBasedSorting = booleanPreferencesKey("time_based_sorting")
    val includeAllDayMeals = booleanPreferencesKey("include_all_day_meals")
}
