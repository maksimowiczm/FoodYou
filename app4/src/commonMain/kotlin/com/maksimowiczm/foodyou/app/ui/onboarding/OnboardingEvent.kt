package com.maksimowiczm.foodyou.app.ui.onboarding

import com.maksimowiczm.foodyou.common.domain.LocalAccountId

sealed interface OnboardingEvent {
    data class Finished(val localAccountId: LocalAccountId) : OnboardingEvent
}
