package com.maksimowiczm.foodyou.app.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class EditProfileViewModel(
    private val profileId: ProfileId,
    private val appAccountManager: AppAccountManager,
    private val accountRepository: AccountRepository,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag(TAG)

    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val canDelete: StateFlow<Boolean> =
        appAccountManager
            .observeAppAccount()
            .map { it.profiles.size > 1 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = false,
            )

    val profile: StateFlow<Profile?> =
        appAccountManager
            .observeAppAccount()
            .map { account -> account.profiles.find { it.id == profileId } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    private val _uiEventBus = Channel<EditProfileEvent>()
    val uiEvents = _uiEventBus.receiveAsFlow()

    fun edit(name: String, avatar: UiProfileAvatar) {
        if (!isLocked.compareAndSet(expect = false, update = true)) {
            logger.w { "Edit profile called while already locked" }
            return
        }

        require(name.isNotBlank()) { "Name cannot be blank" }

        viewModelScope.launch {
            val account = appAccountManager.observeAppAccount().first()

            account.updateProfile(profileId) {
                it.apply {
                    updateName(name)
                    updateAvatar(ProfileAvatarMapper.toModel(avatar))
                }
            }

            accountRepository.save(account)

            _uiEventBus.send(EditProfileEvent.Edited)
        }
    }

    fun delete() {
        if (isLocked.compareAndSet(expect = false, update = true)) {
            logger.w { "Delete profile called while already locked" }
            return
        }

        viewModelScope.launch {
            val account = appAccountManager.observeAppAccount().first()

            account.removeProfile(profileId)

            accountRepository.save(account)

            _uiEventBus.send(EditProfileEvent.Deleted)
        }
    }

    private companion object {
        private const val TAG = "EditProfileViewModel"
    }
}
