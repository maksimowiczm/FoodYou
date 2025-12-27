package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
internal fun rememberOnboardingState(): OnboardingState {
    val openFoodFactsState = rememberSaveable { mutableStateOf(false) }

    return remember(openFoodFactsState) {
        OnboardingState(
            openFoodFactsState = openFoodFactsState,
        )
    }
}

internal class OnboardingState(
    openFoodFactsState: MutableState<Boolean>,
) {
    var useOpenFoodFacts by openFoodFactsState
}
