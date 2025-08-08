package com.maksimowiczm.foodyou.business.settings.domain

data class Settings(
    val lastRememberedVersion: String?,
    val showTranslationWarning: Boolean,
    val nutrientsOrder: List<NutrientsOrder>,
    val secureScreen: Boolean,
    val homeCardOrder: List<HomeCard>,
    val expandGoalCard: Boolean,
)
