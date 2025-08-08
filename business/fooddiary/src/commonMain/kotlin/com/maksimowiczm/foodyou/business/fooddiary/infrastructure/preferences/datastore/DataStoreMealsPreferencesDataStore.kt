package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsCardsLayout
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalMealsPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreMealsPreferencesDataStore(private val dataStore: DataStore<Preferences>) :
    LocalMealsPreferencesDataSource {
    override fun observe(): Flow<MealsPreferences> {
        return dataStore.data.map { preferences ->
            MealsPreferences(
                layout = preferences.getLayout(),
                useTimeBasedSorting =
                    preferences[MealsPreferencesDataStoreKeys.useTimeBasedSorting] ?: false,
                ignoreAllDayMeals =
                    preferences[MealsPreferencesDataStoreKeys.ignoreAllDayMeals] ?: false,
            )
        }
    }

    override suspend fun update(preferences: MealsPreferences) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                set(MealsPreferencesDataStoreKeys.layout, preferences.layout.ordinal)
                set(
                    MealsPreferencesDataStoreKeys.useTimeBasedSorting,
                    preferences.useTimeBasedSorting,
                )
                set(MealsPreferencesDataStoreKeys.ignoreAllDayMeals, preferences.ignoreAllDayMeals)
            }
        }
    }
}

private fun Preferences.getLayout(): MealsCardsLayout =
    this[MealsPreferencesDataStoreKeys.layout]?.let { MealsCardsLayout.entries[it] }
        ?: MealsCardsLayout.default

private object MealsPreferencesDataStoreKeys {
    val layout = intPreferencesKey("fooddiary:meals_preferences:layout")
    val useTimeBasedSorting =
        booleanPreferencesKey("fooddiary:meals_preferences:use_time_based_sorting")
    val ignoreAllDayMeals =
        booleanPreferencesKey("fooddiary:meals_preferences:ignore_all_day_meals")
}
