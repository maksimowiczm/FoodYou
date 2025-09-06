package com.maksimowiczm.foodyou.feature.about.master.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
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
    private val settingsRepository: UserPreferencesRepository<Settings>,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val settingsFlow = settingsRepository.observe()

    init {
        settingsFlow
            .map { it.lastRememberedVersion }
            .onEach {
                if (it != changelog.currentVersion?.version) {
                    settingsRepository.update { copy(hidePreviewDialog = false) }
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

        viewModelScope.launch { settingsRepository.update { copy(hidePreviewDialog = true) } }
    }
}
