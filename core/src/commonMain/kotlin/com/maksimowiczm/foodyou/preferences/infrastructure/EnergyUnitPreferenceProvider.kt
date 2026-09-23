package com.maksimowiczm.foodyou.preferences.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.preferences.domain.EnergyUnitPreference

class EnergyUnitPreferenceProvider(dataStore: DataStore<Preferences>) :
    DataStoreProvider<EnergyUnitPreference>(dataStore) {
    override fun Preferences.map(): EnergyUnitPreference {
        return EnergyUnitPreference(
            energyUnit =
                this[dataStoreKey]?.let { runCatching { EnergyUnit.entries[it] }.getOrNull() }
                    ?: EnergyUnit.Kilocalories
        )
    }

    override fun MutablePreferences.apply(value: EnergyUnitPreference) {
        this[dataStoreKey] = value.energyUnit.ordinal
    }

    override val key: String = EnergyUnitPreference::class.qualifiedName!!

    private companion object {
        val dataStoreKey = intPreferencesKey("device:energy_unit")
    }
}
