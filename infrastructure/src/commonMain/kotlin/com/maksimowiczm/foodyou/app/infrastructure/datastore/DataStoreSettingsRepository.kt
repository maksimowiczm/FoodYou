package com.maksimowiczm.foodyou.app.infrastructure.datastore

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
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import kotlin.collections.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreSettingsRepository(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepository<Settings> {

    override fun observe(): Flow<Settings> = dataStore.data.map(Preferences::toSettings)

    override suspend fun update(transform: Settings.() -> Settings) {
        dataStore.updateData { preferences ->
            val currentSettings = preferences.toSettings()
            val newSettings = currentSettings.transform()
            preferences.toMutablePreferences().applySettings(newSettings)
        }
    }
}

private fun Preferences.toSettings(): Settings =
    Settings(
        lastRememberedVersion = this[SettingsPreferencesKeys.lastRememberedVersion],
        hidePreviewDialog = this[SettingsPreferencesKeys.hidePreviewDialog] ?: false,
        showTranslationWarning = this[SettingsPreferencesKeys.showTranslationWarning] ?: true,
        nutrientsOrder = this.getNutrientsOrder(SettingsPreferencesKeys.nutrientsOrder),
        secureScreen = this[SettingsPreferencesKeys.secureScreen] ?: false,
        homeCardOrder = this.getHomeCardOrder(SettingsPreferencesKeys.homeCardOrder),
        expandGoalCard = this[SettingsPreferencesKeys.expandGoalCard] ?: true,
        onboardingFinished = this[SettingsPreferencesKeys.onboardingFinished] ?: false,
        energyFormat = this.getEnergyFormat(SettingsPreferencesKeys.energyFormat),
    )

private fun MutablePreferences.applySettings(settings: Settings): MutablePreferences = apply {
    setWithNull(SettingsPreferencesKeys.lastRememberedVersion, settings.lastRememberedVersion)
    setWithNull(SettingsPreferencesKeys.hidePreviewDialog, settings.hidePreviewDialog)
    setWithNull(SettingsPreferencesKeys.showTranslationWarning, settings.showTranslationWarning)
    setNutrientsOrder(SettingsPreferencesKeys.nutrientsOrder, settings.nutrientsOrder)
    setWithNull(SettingsPreferencesKeys.secureScreen, settings.secureScreen)
    setHomeCardOrder(SettingsPreferencesKeys.homeCardOrder, settings.homeCardOrder)
    setWithNull(SettingsPreferencesKeys.expandGoalCard, settings.expandGoalCard)
    setWithNull(SettingsPreferencesKeys.onboardingFinished, settings.onboardingFinished)
    setEnergyFormat(SettingsPreferencesKeys.energyFormat, settings.energyFormat)
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
