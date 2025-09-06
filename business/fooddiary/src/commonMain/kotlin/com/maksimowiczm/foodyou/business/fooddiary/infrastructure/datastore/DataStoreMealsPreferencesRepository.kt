package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsCardsLayout
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.core.shared.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreMealsPreferencesRepository(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepository<MealsPreferences> {
    override fun observe(): Flow<MealsPreferences> =
        dataStore.data.map(Preferences::toMealsPreferences)

    override suspend fun update(transform: MealsPreferences.() -> MealsPreferences) {
        dataStore.updateData { preferences ->
            val current = preferences.toMealsPreferences()
            val updated = current.transform()
            preferences.toMutablePreferences().applyMealsPreferences(updated)
        }
    }
}

private fun Preferences.toMealsPreferences(): MealsPreferences =
    MealsPreferences(
        layout = getLayout(),
        useTimeBasedSorting = this[MealsPreferencesDataStoreKeys.useTimeBasedSorting] ?: false,
        ignoreAllDayMeals = this[MealsPreferencesDataStoreKeys.ignoreAllDayMeals] ?: false,
    )

private fun Preferences.getLayout(): MealsCardsLayout =
    this[MealsPreferencesDataStoreKeys.layout]?.let { MealsCardsLayout.entries[it] }
        ?: MealsCardsLayout.default

private fun MutablePreferences.applyMealsPreferences(
    preferences: MealsPreferences
): MutablePreferences = apply {
    this[MealsPreferencesDataStoreKeys.layout] = preferences.layout.ordinal
    this[MealsPreferencesDataStoreKeys.useTimeBasedSorting] = preferences.useTimeBasedSorting
    this[MealsPreferencesDataStoreKeys.ignoreAllDayMeals] = preferences.ignoreAllDayMeals
}

private object MealsPreferencesDataStoreKeys {
    val layout = intPreferencesKey("fooddiary:meals_preferences:layout")
    val useTimeBasedSorting =
        booleanPreferencesKey("fooddiary:meals_preferences:use_time_based_sorting")
    val ignoreAllDayMeals =
        booleanPreferencesKey("fooddiary:meals_preferences:ignore_all_day_meals")
}
