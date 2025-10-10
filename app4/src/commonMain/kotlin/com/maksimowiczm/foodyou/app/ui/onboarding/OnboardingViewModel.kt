package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val createPrimaryAccountUseCase: CreatePrimaryAccountUseCase) :
    ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    private val _finishingOnboarding = MutableStateFlow(false)
    val finishingOnboarding = _finishingOnboarding.asStateFlow()

    private val eventBus = Channel<OnboardingEvent>()
    val events = eventBus.receiveAsFlow()

    fun setAllowFoodYouServices(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowFoodYouServices = allow)
    }

    fun setProfileName(name: String) {
        _uiState.value = _uiState.value.copy(profileName = name)
    }

    fun setAvatar(avatar: UiProfileAvatar) {
        _uiState.value = _uiState.value.copy(avatar = avatar)
    }

    fun setAllowOpenFoodFacts(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowOpenFoodFacts = allow)
    }

    fun setAllowFoodDataCentral(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowFoodDataCentral = allow)
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            _finishingOnboarding.value = true

            val realTask = async { createPrimaryAccountUseCase.execute(_uiState.value) }
            val minDelayTask = async { delay(2_000) }

            val localAccountId = realTask.await()
            minDelayTask.await()

            eventBus.send(OnboardingEvent.Finished(localAccountId))
        }
    }
}
