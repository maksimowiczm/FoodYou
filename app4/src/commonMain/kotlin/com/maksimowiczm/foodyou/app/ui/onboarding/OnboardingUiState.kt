package com.maksimowiczm.foodyou.app.ui.onboarding

data class OnboardingUiState(
    val allowFoodYouServices: Boolean = false,
    val avatar: UiAvatar = UiAvatar.PERSON,
    val profileName: String = "",
)

enum class UiAvatar {
    PERSON,
    WOMAN,
    MAN,
    ENGINEER,
}
