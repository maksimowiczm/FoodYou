package com.maksimowiczm.foodyou.business.settings.domain

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences

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
) : UserPreferences
