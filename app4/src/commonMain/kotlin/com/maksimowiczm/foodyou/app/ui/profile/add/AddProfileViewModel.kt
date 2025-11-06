package com.maksimowiczm.foodyou.app.ui.profile.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.profile.ProfileUiState
import com.maksimowiczm.foodyou.common.fold
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddProfileViewModel(
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag(TAG)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEventBus = Channel<AddProfileEvent>()
    val uiEvents = _uiEventBus.receiveAsFlow()

    fun setAvatar(avatar: UiProfileAvatar) {
        _uiState.value = _uiState.value.copy(avatar = avatar)
    }

    fun create() {
        val state = _uiState.value
        if (state.isLocked) {
            logger.w { "Create profile called while already locked" }
            return
        }

        _uiState.value = state.copy(isLocked = true)

        viewModelScope.launch {
            logger.d { "Loading primary account to add profile" }
            val account = observePrimaryAccountUseCase.observe().first()

            val profile =
                Profile.new(
                    name = state.nameTextState.text.toString(),
                    avatar = ProfileAvatarMapper.toModel(state.avatar),
                )

            account.addProfile(profile)

            accountRepository.save(account)

            accountManager
                .setPrimaryProfileId(profile.id)
                .fold(
                    onSuccess = { _uiEventBus.send(AddProfileEvent.Created(profile.id)) },
                    onError = { error("Failed to set primary profile: $it") },
                )
        }
    }

    private companion object {
        private const val TAG = "AddProfileViewModel"
    }
}
