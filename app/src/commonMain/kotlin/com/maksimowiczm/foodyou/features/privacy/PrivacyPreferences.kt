package com.maksimowiczm.foodyou.features.privacy

import androidx.compose.runtime.*

@Immutable
data class PrivacyPreferences(
    val allowOpenFoodFacts: Boolean,
    val allowFoodDataCentralUSDA: Boolean,
    val isOpenFoodFactsSignedIn: Boolean,
)
