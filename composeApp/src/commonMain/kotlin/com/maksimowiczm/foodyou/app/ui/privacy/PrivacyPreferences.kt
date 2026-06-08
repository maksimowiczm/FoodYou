package com.maksimowiczm.foodyou.app.ui.privacy

import androidx.compose.runtime.*

@Immutable
data class PrivacyPreferences(
    val allowOpenFoodFacts: Boolean,
    val allowFoodDataCentralUSDA: Boolean,
)
