package com.maksimowiczm.foodyou.feature.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.SwissFoodCompositionDatabaseRepository.Language

@Composable
internal fun rememberOnboardingState(): OnboardingState {
    val openFoodFactsState = rememberSaveable { mutableStateOf(false) }
    val usdaState = rememberSaveable { mutableStateOf(false) }
    val swissLanguagesState = rememberSaveable { mutableStateOf<Set<Language>>(emptySet()) }

    return remember(openFoodFactsState, usdaState, swissLanguagesState) {
        OnboardingState(
            openFoodFactsState = openFoodFactsState,
            usdaState = usdaState,
            swissLanguagesState = swissLanguagesState,
        )
    }
}

internal class OnboardingState(
    openFoodFactsState: MutableState<Boolean>,
    usdaState: MutableState<Boolean>,
    swissLanguagesState: MutableState<Set<Language>>,
) {
    var useOpenFoodFacts by openFoodFactsState
    var useUsda by usdaState
    var swissLanguages by swissLanguagesState
}
