package com.maksimowiczm.foodyou.preferences.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.preferences.domain.HideScreenPreference

class HideScreenProvider(dataStore: DataStore<Preferences>) :
    DataStoreProvider<HideScreenPreference>(dataStore) {
    override val key = HideScreenPreference::class.qualifiedName!!

    override fun Preferences.map(): HideScreenPreference =
        HideScreenPreference(hideScreen = this[dataStoreKey] ?: false)

    override fun MutablePreferences.apply(value: HideScreenPreference) {
        this[dataStoreKey] = value.hideScreen
    }

    private companion object {
        private val dataStoreKey = booleanPreferencesKey("device:hide_screen")
    }
}
