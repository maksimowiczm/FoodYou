package com.maksimowiczm.foodyou.app.ui.onboarding

fun interface CreatePrimaryAccountUseCase {
    suspend fun execute(uiState: OnboardingUiState)
}
