package com.maksimowiczm.foodyou.features.onboarding

internal sealed interface OnboardingEvent {
    data object Finished : OnboardingEvent
}
