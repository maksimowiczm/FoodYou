package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.saveable.jsonSaver

@Stable
internal class OnboardingState(
    val nameTextFieldState: TextFieldState,
    avatarState: MutableState<UiProfileAvatar>,
    allowOpenFoodFactsState: MutableState<Boolean>,
    allowFoodDataCentralState: MutableState<Boolean>,
) {
    var profileAvatar: UiProfileAvatar by avatarState
    var allowOpenFoodFacts: Boolean by allowOpenFoodFactsState
    var allowFoodDataCentral: Boolean by allowFoodDataCentralState

    val isValid by derivedStateOf { nameTextFieldState.text.isNotBlank() }
}

@Composable
internal fun rememberOnboardingState(): OnboardingState {
    val nameTextFieldState = rememberTextFieldState()
    val avatarState =
        rememberSaveable(stateSaver = jsonSaver()) {
            mutableStateOf<UiProfileAvatar>(
                UiProfileAvatar.Predefined(UiProfileAvatar.Predefined.Variant.PERSON)
            )
        }
    val allowOpenFoodFactsState = rememberSaveable { mutableStateOf(false) }
    val allowFoodDataCentralState = rememberSaveable { mutableStateOf(false) }

    return remember(
        nameTextFieldState,
        avatarState,
        allowOpenFoodFactsState,
        allowFoodDataCentralState,
    ) {
        OnboardingState(
            nameTextFieldState = nameTextFieldState,
            avatarState = avatarState,
            allowOpenFoodFactsState = allowOpenFoodFactsState,
            allowFoodDataCentralState = allowFoodDataCentralState,
        )
    }
}
