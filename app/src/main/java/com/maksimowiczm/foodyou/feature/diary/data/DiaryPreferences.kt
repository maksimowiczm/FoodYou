package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object DiaryPreferences {
    val selectedDateEpoch = longPreferencesKey("SELECTED_DATE_EPOCH")

    val caloriesGoal = intPreferencesKey("CALORIES_GOAL")
    val proteinsGoal = floatPreferencesKey("PROTEINS_GOAL")
    val carbohydratesGoal = floatPreferencesKey("CARBOHYDRATES_GOAL")
    val fatsGoal = floatPreferencesKey("FATS_GOAL")

    val breakfastCalories = floatPreferencesKey("BREAKFAST_CALORIES")
    val lunchCalories = floatPreferencesKey("LUNCH_CALORIES")
    val dinnerCalories = floatPreferencesKey("DINNER_CALORIES")
    val snacksCalories = floatPreferencesKey("SNACKS_CALORIES")
}
