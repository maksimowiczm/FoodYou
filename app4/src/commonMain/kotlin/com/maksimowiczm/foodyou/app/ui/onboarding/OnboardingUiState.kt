package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar

@Immutable
data class OnboardingUiState(
    val allowFoodYouServices: Boolean = false,
    val allowOpenFoodFacts: Boolean = false,
    val allowFoodDataCentral: Boolean = false,
    val avatar: UiProfileAvatar = UiProfileAvatar.PERSON,
    val profileName: String = "",
) {
    val isProfileValid: Boolean
        get() = profileName.isNotBlank()
}
