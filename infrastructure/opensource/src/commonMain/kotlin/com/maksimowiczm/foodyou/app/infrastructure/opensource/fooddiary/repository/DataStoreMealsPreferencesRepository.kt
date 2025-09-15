package com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.maksimowiczm.foodyou.app.business.shared.domain.fooddiary.MealsCardsLayout
import com.maksimowiczm.foodyou.app.business.shared.domain.fooddiary.MealsPreferences
import com.maksimowiczm.foodyou.app.infrastructure.opensource.shared.datastore.AbstractDataStoreUserPreferencesRepository

internal class DataStoreMealsPreferencesRepository(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<MealsPreferences>(dataStore) {
    override fun Preferences.toUserPreferences(): MealsPreferences =
        MealsPreferences(
            layout = getLayout(),
            useTimeBasedSorting = this[MealsPreferencesDataStoreKeys.useTimeBasedSorting] ?: false,
            ignoreAllDayMeals = this[MealsPreferencesDataStoreKeys.ignoreAllDayMeals] ?: false,
        )

    override fun MutablePreferences.applyUserPreferences(updated: MealsPreferences) {
        this[MealsPreferencesDataStoreKeys.layout] = updated.layout.ordinal
        this[MealsPreferencesDataStoreKeys.useTimeBasedSorting] = updated.useTimeBasedSorting
        this[MealsPreferencesDataStoreKeys.ignoreAllDayMeals] = updated.ignoreAllDayMeals
    }
}

private fun Preferences.getLayout(): MealsCardsLayout =
    runCatching { this[MealsPreferencesDataStoreKeys.layout]?.let { MealsCardsLayout.entries[it] } }
        .getOrNull() ?: MealsCardsLayout.default

private object MealsPreferencesDataStoreKeys {
    val layout = intPreferencesKey("fooddiary:meals_preferences:layout")
    val useTimeBasedSorting =
        booleanPreferencesKey("fooddiary:meals_preferences:use_time_based_sorting")
    val ignoreAllDayMeals =
        booleanPreferencesKey("fooddiary:meals_preferences:ignore_all_day_meals")
}
