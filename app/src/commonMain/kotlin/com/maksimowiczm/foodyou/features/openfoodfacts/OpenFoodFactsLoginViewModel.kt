package com.maksimowiczm.foodyou.features.openfoodfacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.crypto.SoftwareCrypto
import com.maksimowiczm.foodyou.common.domain.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.common.domain.crypto.encryptString
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsCredentials
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsLoginService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OpenFoodFactsLoginViewModel(
    private val service: OpenFoodFactsLoginService,
    private val repository: OpenFoodFactsSettingsRepository,
    private val crypto: SoftwareCrypto,
) : ViewModel() {
    private val signInUiEventBus = Channel<OpenFoodFactsLoginSignedInUiEvent>()
    val signInUiEvent = signInUiEventBus.receiveAsFlow()

    val uiState: StateFlow<OpenFoodFactsLoginUiState>
        field = MutableStateFlow(OpenFoodFactsLoginUiState())

    fun login(login: String, password: String) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(inProgress = true, authenticationFailure = false)

            runCatching {
                service.login(login, password)
            }
                .onSuccess {
                    repository.update {
                        it.copy(
                            credentials =
                                OpenFoodFactsCredentials(
                                    login = SoftwareEncrypted.encryptString(login, crypto),
                                    password = SoftwareEncrypted.encryptString(password, crypto),
                                )
                        )
                    }
                    signInUiEventBus.send(OpenFoodFactsLoginSignedInUiEvent)
                }
                .onFailure {
                    uiState.value = uiState.value.copy(authenticationFailure = true)
                }

            uiState.value = uiState.value.copy(inProgress = false)
        }
    }
}
