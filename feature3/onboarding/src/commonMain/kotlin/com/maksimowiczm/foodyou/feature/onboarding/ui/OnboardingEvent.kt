package com.maksimowiczm.foodyou.feature.onboarding.ui

internal sealed interface OnboardingEvent {
    data object Finish : OnboardingEvent
}
