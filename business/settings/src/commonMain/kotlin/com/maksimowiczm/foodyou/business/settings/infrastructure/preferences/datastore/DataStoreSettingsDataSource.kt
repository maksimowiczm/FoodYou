package com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.settings.domain.EnergyFormat
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
                hidePreviewDialog = preferences[SettingsPreferencesKeys.hidePreviewDialog] ?: false,
                showTranslationWarning =
                    preferences[SettingsPreferencesKeys.showTranslationWarning] ?: true,
                nutrientsOrder =
                    preferences.getNutrientsOrder(SettingsPreferencesKeys.nutrientsOrder),
                secureScreen = preferences[SettingsPreferencesKeys.secureScreen] ?: false,
                homeCardOrder = preferences.getHomeCardOrder(SettingsPreferencesKeys.homeCardOrder),
                expandGoalCard = preferences[SettingsPreferencesKeys.expandGoalCard] ?: true,
                onboardingFinished =
                    preferences[SettingsPreferencesKeys.onboardingFinished] ?: false,
                energyFormat = preferences.getEnergyFormat(SettingsPreferencesKeys.energyFormat),
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

    override suspend fun updateOnboardingFinished(onboardingFinished: Boolean) {
        updateData { set(SettingsPreferencesKeys.onboardingFinished, onboardingFinished) }
    }

    override suspend fun updateHidePreviewDialog(hidePreviewDialog: Boolean) {
        updateData { set(SettingsPreferencesKeys.hidePreviewDialog, hidePreviewDialog) }
    }

    override suspend fun updateEnergyFormat(energyFormat: EnergyFormat) {
        updateData { setEnergyFormat(SettingsPreferencesKeys.energyFormat, energyFormat) }
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

private fun MutablePreferences.setEnergyFormat(key: Preferences.Key<Int>, value: EnergyFormat) =
    setWithNull(key, value.ordinal)

private fun Preferences.getEnergyFormat(key: Preferences.Key<Int>): EnergyFormat =
    runCatching { EnergyFormat.entries[this[key] ?: EnergyFormat.DEFAULT.ordinal] }
        .getOrElse { EnergyFormat.DEFAULT }

private object SettingsPreferencesKeys {
    val lastRememberedVersion = stringPreferencesKey("settings:lastRememberedVersion")
    val hidePreviewDialog = booleanPreferencesKey("settings:hidePreviewDialog")
    val showTranslationWarning = booleanPreferencesKey("settings:showTranslationWarning")
    val nutrientsOrder = stringPreferencesKey("settings:nutrientsOrder")
    val secureScreen = booleanPreferencesKey("settings:secureScreen")
    val homeCardOrder = stringPreferencesKey("settings:homeCardOrder")
    val expandGoalCard = booleanPreferencesKey("settings:expandGoalCard")
    val onboardingFinished = booleanPreferencesKey("settings:onboardingFinished")
    val energyFormat = intPreferencesKey("settings:energyFormat")
}
