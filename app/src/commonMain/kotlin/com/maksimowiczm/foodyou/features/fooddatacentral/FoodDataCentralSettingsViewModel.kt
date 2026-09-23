package com.maksimowiczm.foodyou.features.fooddatacentral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.infrastructure.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.common.infrastructure.crypto.encryptString
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiKeyVerificationService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FoodDataCentralSettingsViewModel(
    private val service: FoodDataCentralApiKeyVerificationService,
    private val settingsRepository: FoodDataCentralSettingsRepository,
) : ViewModel() {
    private val uiEventBus = Channel<FoodDataCentralSettingsUiEvent>()
    val uiEvent = uiEventBus.receiveAsFlow()

    val uiState: StateFlow<FoodDataCentralSettingUiState>
        field = MutableStateFlow(FoodDataCentralSettingUiState())

    fun verifyAndSave(apiKey: String) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(inProgress = true)

            runCatching {
                service.verify(apiKey)
            }
                .onSuccess {
                    settingsRepository.update {
                        it.copy(apiKey = SoftwareEncrypted.encryptString(apiKey))
                    }
                    uiEventBus.send(FoodDataCentralSettingsUiEvent.Saved)
                }
                .onFailure {
                    uiState.value = uiState.value.copy(error = it)
                }

            uiState.value = uiState.value.copy(inProgress = false)
        }
    }
}

data class FoodDataCentralSettingUiState(
    val inProgress: Boolean = false,
    val apiKey: String? = null,
    val error: Throwable? = null,
)

sealed interface FoodDataCentralSettingsUiEvent {
    data object Saved : FoodDataCentralSettingsUiEvent
}
