package com.maksimowiczm.foodyou.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountManager: AccountManager,
    observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
) : ViewModel() {
    val profiles =
        observePrimaryAccountUseCase
            .observe()
            .filterNotNull()
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
        accountManager
            .observePrimaryProfileId()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    fun selectProfile(profile: ProfileUiState) {
        viewModelScope.launch { accountManager.setPrimaryProfileId(profile.id) }
    }
}
