package com.maksimowiczm.foodyou.app.business.opensource.domain.settings

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.map

data class Settings(
    val lastRememberedVersion: String?,
    val hidePreviewDialog: Boolean,
    val showTranslationWarning: Boolean,
    val nutrientsOrder: List<NutrientsOrder>,
    val secureScreen: Boolean,
    val homeCardOrder: List<HomeCard>,
    val expandGoalCard: Boolean,
    val onboardingFinished: Boolean,
    val energyFormat: EnergyFormat,
    val appLaunchInfo: AppLaunchInfo,
    val themeSettings: ThemeSettings,
    val nutrientsColors: NutrientsColors,
) : UserPreferences

fun UserPreferencesRepository<Settings>.observeThemeSettings() = observe().map { it.themeSettings }

suspend fun UserPreferencesRepository<Settings>.updateThemeSettings(
    transform: ThemeSettings.() -> ThemeSettings
) = update { copy(themeSettings = themeSettings.transform()) }

fun UserPreferencesRepository<Settings>.observeNutrientsColors() =
    observe().map { it.nutrientsColors }

suspend fun UserPreferencesRepository<Settings>.updateNutrientsColors(
    transform: NutrientsColors.() -> NutrientsColors
) = update { copy(nutrientsColors = nutrientsColors.transform()) }
