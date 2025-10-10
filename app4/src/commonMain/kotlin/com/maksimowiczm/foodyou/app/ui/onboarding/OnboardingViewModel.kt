package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    fun setAllowFoodYouServices(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowFoodYouServices = allow)
    }
}
