package com.maksimowiczm.foodyou.feature.about.master.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.changelog.ObserveChangelogUseCase
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.Settings
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ChangelogViewModel(
    observeChangelogUseCase: ObserveChangelogUseCase,
    private val settingsRepository: UserPreferencesRepository<Settings>,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _changelog = observeChangelogUseCase.observe()
    val changelog =
        _changelog.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private val isPreviewRelease =
        _changelog
            .map { it.currentVersion?.isPreview ?: true }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    private val hidePreviewDialogSetting = settingsRepository.observe().map { it.hidePreviewDialog }
    private val hide = savedStateHandle.getStateFlow("hide", false)

    val showDialog =
        combine(isPreviewRelease, hide, hidePreviewDialogSetting) { isPreview, hide, hideSetting ->
                if (!isPreview) {
                    false
                } else {
                    !hide && !hideSetting
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    fun dismissDialog() {
        savedStateHandle["hide"] = true
    }

    fun dontShowAgain() {
        savedStateHandle["hide"] = true

        viewModelScope.launch { settingsRepository.update { copy(hidePreviewDialog = true) } }
    }
}
