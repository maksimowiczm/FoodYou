package com.maksimowiczm.foodyou.feature.settings.personalization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.SetSecureScreenCommand
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class PersonalizationScreenViewModel(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val commandBus: CommandBus,
) : ViewModel() {

    private val _secureScreen = observeSettingsUseCase.observe().map { it.secureScreen }

    val secureScreen =
        _secureScreen.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _secureScreen.first() },
        )

    fun toggleSecureScreen(newState: Boolean) {
        viewModelScope.launch { commandBus.dispatch(SetSecureScreenCommand(newState)) }
    }
}
