package com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.settings.domain.HomeCard
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
                secureScreen = preferences[SettingsPreferencesKeys.secureScreen] ?: false,
                homeCardOrder = preferences.getHomeCardOrder(SettingsPreferencesKeys.homeCardOrder),
                expandGoalCard = preferences[SettingsPreferencesKeys.expandGoalCard] ?: true,
            )
        }

    override suspend fun updateLastRememberedVersion(version: String) {
        updateData { setWithNull(SettingsPreferencesKeys.lastRememberedVersion, version) }
    }

    override suspend fun updateShowTranslationWarning(show: Boolean) {
        updateData { set(SettingsPreferencesKeys.showTranslationWarning, show) }
    }

    override suspend fun updateSecureScreen(secureScreen: Boolean) {
        updateData { set(SettingsPreferencesKeys.secureScreen, secureScreen) }
    }

    override suspend fun updateNutrientsOrder(order: List<NutrientsOrder>) {
        updateData { setNutrientsOrder(SettingsPreferencesKeys.nutrientsOrder, order) }
    }

    override suspend fun updateHomeCardOrder(order: List<HomeCard>) {
        updateData { setHomeCardOrder(SettingsPreferencesKeys.homeCardOrder, order) }
    }

    override suspend fun updateExpandGoalCard(expand: Boolean) {
        updateData { set(SettingsPreferencesKeys.expandGoalCard, expand) }
    }

    private suspend fun updateData(transform: suspend MutablePreferences.() -> Unit) {
        dataStore.updateData { it.toMutablePreferences().apply { transform() } }
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

private fun MutablePreferences.setHomeCardOrder(
    key: Preferences.Key<String>,
    value: List<HomeCard>,
) = setWithNull(key, value.joinToString(",") { it.ordinal.toString() })

private fun Preferences.getHomeCardOrder(key: Preferences.Key<String>): List<HomeCard> =
    runCatching { this[key]?.split(",")?.map { HomeCard.entries[it.toInt()] } }.getOrNull()
        ?: HomeCard.defaultOrder

private object SettingsPreferencesKeys {
    val lastRememberedVersion = stringPreferencesKey("settings:lastRememberedVersion")
    val showTranslationWarning = booleanPreferencesKey("settings:showTranslationWarning")
    val nutrientsOrder = stringPreferencesKey("settings:nutrientsOrder")
    val secureScreen = booleanPreferencesKey("settings:secureScreen")
    val homeCardOrder = stringPreferencesKey("settings:homeCardOrder")
    val expandGoalCard = booleanPreferencesKey("settings:expandGoalCard")
}
