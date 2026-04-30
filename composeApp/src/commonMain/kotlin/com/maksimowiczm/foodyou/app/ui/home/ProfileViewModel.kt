package com.maksimowiczm.foodyou.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ProfileViewModel(
    private val appProfileManager: AppProfileManager,
    accountRepository: AccountRepository,
) : ViewModel() {
    val profiles =
        accountRepository
            .observe()
            .filterNotNull()
            .map { account ->
                account.profiles.map { profile ->
                    ProfileUiState(id = profile.id, name = profile.name, avatar = profile.avatar)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    val selectedProfile =
        appProfileManager
            .observeAppProfileId()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    fun selectProfile(profile: ProfileUiState) {
        viewModelScope.launch { appProfileManager.setAppProfileId(profile.id) }
    }
}
