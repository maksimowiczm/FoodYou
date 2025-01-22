package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object DiaryPreferences {
    val selectedDateEpoch = longPreferencesKey("selected_date_epoch")

    val caloriesGoal = intPreferencesKey("calories_goal")
    val proteinsGoal = floatPreferencesKey("proteins_goal")
    val carbohydratesGoal = floatPreferencesKey("carbohydrates_goal")
    val fatsGoal = floatPreferencesKey("fats_goal")

    val breakfastCalories = floatPreferencesKey("breakfast_calories")
    val lunchCalories = floatPreferencesKey("lunch_calories")
    val dinnerCalories = floatPreferencesKey("dinner_calories")
    val snacksCalories = floatPreferencesKey("snacks_calories")
}
