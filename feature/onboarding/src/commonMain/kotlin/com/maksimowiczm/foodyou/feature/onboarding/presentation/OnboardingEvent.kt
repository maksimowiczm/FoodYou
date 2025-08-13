package com.maksimowiczm.foodyou.feature.onboarding.presentation

internal sealed interface OnboardingEvent {
    data object Finished : OnboardingEvent
}
