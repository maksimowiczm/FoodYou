package com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import kotlin.collections.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreSettingsDataSource(private val dataStore: DataStore<Preferences>) :
    LocalSettingsDataSource {

    override fun observe(): Flow<Settings> =
        dataStore.data.map { preferences ->
            Settings(
                lastRememberedVersion = preferences[SettingsPreferencesKeys.lastRememberedVersion],
                showTranslationWarning =
                    preferences[SettingsPreferencesKeys.showTranslationWarning] ?: true,
                nutrientsOrder =
                    preferences.getNutrientsOrder(SettingsPreferencesKeys.nutrientsOrder),
            )
        }

    override suspend fun update(settings: Settings) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                setWithNull(
                    SettingsPreferencesKeys.lastRememberedVersion,
                    settings.lastRememberedVersion,
                )

                set(SettingsPreferencesKeys.showTranslationWarning, settings.showTranslationWarning)

                setNutrientsOrder(SettingsPreferencesKeys.nutrientsOrder, settings.nutrientsOrder)
            }
        }
    }

    override suspend fun updateShowTranslationWarning(show: Boolean) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                set(SettingsPreferencesKeys.showTranslationWarning, show)
            }
        }
    }
}

private fun <T> MutablePreferences.setWithNull(key: Preferences.Key<T>, value: T?) {
    if (value != null) {
        this[key] = value
    } else {
        this.remove(key)
    }
}

private fun MutablePreferences.setNutrientsOrder(
    key: Preferences.Key<String>,
    value: List<NutrientsOrder>,
) = setWithNull(key, value.joinToString(",") { it.ordinal.toString() })

private fun Preferences.getNutrientsOrder(key: Preferences.Key<String>): List<NutrientsOrder> =
    runCatching { this[key]?.split(",")?.map { NutrientsOrder.entries[it.toInt()] } }.getOrNull()
        ?: NutrientsOrder.defaultOrder

private object SettingsPreferencesKeys {
    val lastRememberedVersion = stringPreferencesKey("settings:lastRememberedVersion")
    val showTranslationWarning = booleanPreferencesKey("settings:showTranslationWarning")
    val nutrientsOrder = stringPreferencesKey("settings:nutrientsOrder")
}
