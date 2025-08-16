package com.maksimowiczm.foodyou.feature.about.master.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQuery
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class PreviewReleaseDialogViewModel(
    changelog: Changelog,
    queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val settingsFlow = queryBus.dispatch(ObserveSettingsQuery)

    init {
        settingsFlow
            .map { it.lastRememberedVersion }
            .onEach {
                if (it != changelog.currentVersion?.version) {
                    commandBus.dispatch(PartialSettingsUpdateCommand(hidePreviewDialog = false))
                }
            }
            .launchIn(viewModelScope)
    }

    private val isPreviewRelease = changelog.currentVersion?.isPreview ?: true
    private val hidePreviewDialogSetting = settingsFlow.map { it.hidePreviewDialog }
    private val hide = savedStateHandle.getStateFlow("hide", false)

    val showDialog =
        if (!isPreviewRelease) {
            MutableStateFlow(false).asStateFlow()
        } else {
            combine(hide, hidePreviewDialogSetting) { hide, hideSetting ->
                    if (hide) {
                        false
                    } else {
                        !hideSetting
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = false,
                )
        }

    fun dismissDialog() {
        savedStateHandle["hide"] = true
    }

    fun dontShowAgain() {
        savedStateHandle["hide"] = true

        viewModelScope.launch {
            commandBus.dispatch(PartialSettingsUpdateCommand(hidePreviewDialog = true))
        }
    }
}
