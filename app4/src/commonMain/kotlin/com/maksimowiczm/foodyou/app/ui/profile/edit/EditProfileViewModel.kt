package com.maksimowiczm.foodyou.app.ui.profile.edit

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.component.toProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.component.toUiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.profile.ProfileUiState
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val profileId: ProfileId,
    private val accountRepository: AccountRepository,
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag(TAG)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEventBus = Channel<EditProfileEvent>()
    val uiEvents = _uiEventBus.receiveAsFlow()

    init {
        viewModelScope.launch {
            logger.d { "Loading primary account to edit profile" }
            val account = observePrimaryAccountUseCase.observe().first()
            val profile = account.profiles.find { it.id == profileId }

            if (profile != null) {
                _uiState.value =
                    ProfileUiState(
                        nameTextState = TextFieldState(profile.name),
                        avatar = profile.avatar.toUiProfileAvatar(),
                        defaultName = profile.name,
                        defaultAvatar = profile.avatar.toUiProfileAvatar(),
                    )
            } else {
                error(
                    "Profile with id $profileId not found in primary account ${account.localAccountId}"
                )
            }
        }
    }

    fun setAvatar(avatar: UiProfileAvatar) {
        _uiState.value = _uiState.value.copy(avatar = avatar)
    }

    fun edit() {
        val state = _uiState.value
        if (state.isLocked) {
            logger.w { "Create profile called while already locked" }
            return
        }

        _uiState.value = state.copy(isLocked = true)

        viewModelScope.launch {
            val account = observePrimaryAccountUseCase.observe().first()

            account.updateProfile(profileId) {
                it.apply {
                    updateName(state.nameTextState.text.toString())
                    updateAvatar(state.avatar.toProfileAvatar())
                }
            }

            accountRepository.save(account)

            _uiEventBus.send(EditProfileEvent.Edited)
        }
    }

    private companion object {
        private const val TAG = "EditProfileViewModel"
    }
}
