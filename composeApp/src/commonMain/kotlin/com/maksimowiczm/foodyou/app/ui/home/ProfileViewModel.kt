package com.maksimowiczm.foodyou.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val appAccountManager: AppAccountManager) : ViewModel() {
    val profiles =
        appAccountManager
            .observeAppAccount()
            .map { account ->
                account.profiles.map { profile ->
                    ProfileUiState(
                        id = profile.id,
                        name = profile.name,
                        avatar = ProfileAvatarMapper.toUiModel(profile.avatar),
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    val selectedProfile =
        appAccountManager
            .observeAppProfileId()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    fun selectProfile(profile: ProfileUiState) {
        viewModelScope.launch { appAccountManager.setAppProfileId(profile.id) }
    }
}
