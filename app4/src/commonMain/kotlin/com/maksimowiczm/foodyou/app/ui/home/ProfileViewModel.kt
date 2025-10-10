package com.maksimowiczm.foodyou.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    val profiles =
        accountManager
            .observePrimaryAccountId()
            .filterNotNull()
            .flatMapLatest { accountId ->
                accountRepository.observe(accountId).filterNotNull().map { it.profiles }
            }
            .map {
                it.map { profile ->
                    ProfileUiState(
                        id = profile.id,
                        name = profile.name,
                        avatar =
                            when (profile.avatar) {
                                Profile.Avatar.PERSON -> UiProfileAvatar.PERSON
                                Profile.Avatar.WOMAN -> UiProfileAvatar.WOMAN
                                Profile.Avatar.MAN -> UiProfileAvatar.MAN
                                Profile.Avatar.ENGINEER -> UiProfileAvatar.ENGINEER
                            },
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
