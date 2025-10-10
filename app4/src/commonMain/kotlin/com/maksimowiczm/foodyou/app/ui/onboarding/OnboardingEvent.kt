package com.maksimowiczm.foodyou.app.ui.onboarding

sealed interface OnboardingEvent {
    data object Finished : OnboardingEvent
}
