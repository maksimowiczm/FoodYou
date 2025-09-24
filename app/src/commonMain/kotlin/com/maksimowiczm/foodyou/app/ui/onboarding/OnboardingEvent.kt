package com.maksimowiczm.foodyou.app.ui.onboarding

internal sealed interface OnboardingEvent {
    data object Finished : OnboardingEvent
}
