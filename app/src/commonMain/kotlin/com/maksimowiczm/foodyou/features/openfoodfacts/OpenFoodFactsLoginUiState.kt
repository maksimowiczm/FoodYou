package com.maksimowiczm.foodyou.features.openfoodfacts

import androidx.compose.runtime.*

@Immutable
data class OpenFoodFactsLoginUiState(
    val inProgress: Boolean = false,
    val authenticationFailure: Boolean = false,
)
