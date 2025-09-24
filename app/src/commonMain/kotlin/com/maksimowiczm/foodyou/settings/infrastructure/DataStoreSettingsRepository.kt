package com.maksimowiczm.foodyou.settings.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.infrastructure.datastore.AbstractDataStoreUserPreferencesRepository
import com.maksimowiczm.foodyou.common.infrastructure.datastore.set
import com.maksimowiczm.foodyou.settings.domain.entity.AppLaunchInfo
import com.maksimowiczm.foodyou.settings.domain.entity.EnergyFormat
import com.maksimowiczm.foodyou.settings.domain.entity.HomeCard
import com.maksimowiczm.foodyou.settings.domain.entity.NutrientsOrder
import com.maksimowiczm.foodyou.settings.domain.entity.Settings
import kotlin.time.Instant

internal class DataStoreSettingsRepository(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<Settings>(dataStore) {
    override fun Preferences.toUserPreferences(): Settings =
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
            appLaunchInfo = this.getAppLaunchInfo(),
        )

    override fun MutablePreferences.applyUserPreferences(updated: Settings) {
        this[SettingsPreferencesKeys.lastRememberedVersion] = updated.lastRememberedVersion
        this[SettingsPreferencesKeys.hidePreviewDialog] = updated.hidePreviewDialog
        this[SettingsPreferencesKeys.showTranslationWarning] = updated.showTranslationWarning
        setNutrientsOrder(SettingsPreferencesKeys.nutrientsOrder, updated.nutrientsOrder)
        this[SettingsPreferencesKeys.secureScreen] = updated.secureScreen
        setHomeCardOrder(SettingsPreferencesKeys.homeCardOrder, updated.homeCardOrder)
        this[SettingsPreferencesKeys.expandGoalCard] = updated.expandGoalCard
        this[SettingsPreferencesKeys.onboardingFinished] = updated.onboardingFinished
        setEnergyFormat(SettingsPreferencesKeys.energyFormat, updated.energyFormat)
        setAppLaunchInfo(updated.appLaunchInfo)
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

private fun Preferences.getAppLaunchInfo(): AppLaunchInfo =
    AppLaunchInfo(
        firstLaunch = getInstantFromEpochSeconds(SettingsPreferencesKeys.firstLaunchEpoch),
        firstLaunchCurrentVersion =
            run {
                val version = this[SettingsPreferencesKeys.firstLaunchCurrentVersionName]
                val epoch =
                    getInstantFromEpochSeconds(
                        SettingsPreferencesKeys.firstLaunchCurrentVersionEpoch
                    )
                if (version != null && epoch != null) version to epoch else null
            },
        launchesCount = this[SettingsPreferencesKeys.launchesCount] ?: 0,
    )

private fun MutablePreferences.setAppLaunchInfo(appLaunchInfo: AppLaunchInfo): MutablePreferences =
    apply {
        setInstantAsEpochSeconds(
            SettingsPreferencesKeys.firstLaunchEpoch,
            appLaunchInfo.firstLaunch,
        )
        setWithNull(
            SettingsPreferencesKeys.firstLaunchCurrentVersionName,
            appLaunchInfo.firstLaunchCurrentVersion?.first,
        )
        setInstantAsEpochSeconds(
            SettingsPreferencesKeys.firstLaunchCurrentVersionEpoch,
            appLaunchInfo.firstLaunchCurrentVersion?.second,
        )
        setWithNull(SettingsPreferencesKeys.launchesCount, appLaunchInfo.launchesCount)
    }

private fun Preferences.getInstantFromEpochSeconds(key: Preferences.Key<Long>): Instant? =
    this[key]?.let(Instant::fromEpochSeconds)

private fun MutablePreferences.setInstantAsEpochSeconds(
    key: Preferences.Key<Long>,
    value: Instant?,
) =
    when (val epochSeconds = value?.epochSeconds) {
        null -> remove(key)
        else -> this[key] = epochSeconds
    }

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
    val firstLaunchEpoch = longPreferencesKey("first_launch_epoch")
    val firstLaunchCurrentVersionName = stringPreferencesKey("first_launch_current_version_name")
    val firstLaunchCurrentVersionEpoch = longPreferencesKey("first_launch_current_version_epoch")
    val launchesCount = intPreferencesKey("launches_count")
}
