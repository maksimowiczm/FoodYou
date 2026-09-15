package com.maksimowiczm.foodyou.preferences.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.preferences.domain.NutrientsOrderPreference

class NutrientsOrderPreferenceProvider(dataStore: DataStore<Preferences>) :
    DataStoreProvider<NutrientsOrderPreference>(dataStore) {
    override fun Preferences.map(): NutrientsOrderPreference {
        return this[dataStoreKey]?.let { stored ->
            runCatching {
                NutrientsOrderPreference(
                    stored
                        .split(",")
                        .mapNotNull { it.toIntOrNull() }
                        .map { NutrientsOrder.entries[it] }
                )
            }
                .getOrNull()
        } ?: NutrientsOrderPreference(NutrientsOrder.defaultOrder)
    }

    override fun MutablePreferences.apply(value: NutrientsOrderPreference) {
        this[dataStoreKey] = value.nutrientsOrder.map { it.ordinal }.joinToString(",")
    }

    override val key: String = NutrientsOrderPreference::class.qualifiedName!!

    private companion object {
        private val dataStoreKey = stringPreferencesKey("device:nutrientsOrder")
    }
}
