package com.maksimowiczm.foodyou.app.ui.onboarding.opensource

internal sealed interface OnboardingEvent {
    data object Finished : OnboardingEvent
}
