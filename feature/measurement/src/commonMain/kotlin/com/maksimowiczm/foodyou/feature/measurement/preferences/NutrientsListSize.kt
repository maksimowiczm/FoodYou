package com.maksimowiczm.foodyou.feature.measurement.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

internal enum class NutrientsListSize {
    Compact,
    Full;

    fun toggle() = when (this) {
        Compact -> Full
        Full -> Compact
    }
}

internal class NutrientsListSizePreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Boolean, NutrientsListSize>(
        dataStore = dataStore,
        key = booleanPreferencesKey("measurement.nutrients_list_size")
    ) {
    override fun Boolean?.toValue() = when (this) {
        true -> NutrientsListSize.Full
        false, null -> NutrientsListSize.Compact
    }

    override fun NutrientsListSize.toStore() = when (this) {
        NutrientsListSize.Compact -> false
        NutrientsListSize.Full -> true
    }
}
