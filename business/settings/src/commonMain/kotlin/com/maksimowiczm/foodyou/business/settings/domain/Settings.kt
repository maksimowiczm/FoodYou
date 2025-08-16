package com.maksimowiczm.foodyou.business.settings.domain

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
)
