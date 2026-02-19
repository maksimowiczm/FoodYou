package com.maksimowiczm.foodyou.app.ui.onboarding

import com.maksimowiczm.foodyou.common.domain.LocalAccountId

internal sealed interface OnboardingEvent {
    data class Finished(val localAccountId: LocalAccountId) : OnboardingEvent
}
