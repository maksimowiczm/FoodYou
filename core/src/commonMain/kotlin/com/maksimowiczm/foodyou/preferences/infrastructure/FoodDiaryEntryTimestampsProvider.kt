package com.maksimowiczm.foodyou.preferences.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.preferences.domain.FoodDiaryEntryTimestampsPreference

class FoodDiaryEntryTimestampsProvider(dataStore: DataStore<Preferences>) :
    DataStoreProvider<FoodDiaryEntryTimestampsPreference>(dataStore) {
    override val key = FoodDiaryEntryTimestampsPreference::class.qualifiedName!!

    override fun Preferences.map(): FoodDiaryEntryTimestampsPreference =
        FoodDiaryEntryTimestampsPreference(enabled = this[dataStoreKey] ?: true)

    override fun MutablePreferences.apply(value: FoodDiaryEntryTimestampsPreference) {
        this[dataStoreKey] = value.enabled
    }

    private companion object {
        private val dataStoreKey = booleanPreferencesKey("user:food_diary_entry_timestamps")
    }
}
