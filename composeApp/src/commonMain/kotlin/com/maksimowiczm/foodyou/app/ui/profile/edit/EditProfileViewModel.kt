package com.maksimowiczm.foodyou.app.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.domain.update
import com.maksimowiczm.foodyou.account.domain.updateProfile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class EditProfileViewModel(
    private val profileId: ProfileId,
    private val appProfileManager: AppProfileManager,
    private val accountRepository: AccountRepository,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag(TAG)

    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val canDelete: StateFlow<Boolean> =
        accountRepository
            .observe()
            .filterNotNull()
            .map { it.profiles.size > 1 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = false,
            )

    val profile: StateFlow<Profile?> =
        accountRepository
            .observe()
            .filterNotNull()
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
            accountRepository.update {
                updateProfile(profileId) {
                    it.copy(name = name, avatar = ProfileAvatarMapper.toModel(avatar))
                }
            }
            _uiEventBus.send(EditProfileEvent.Edited)
        }
    }

    fun delete() {
        if (!isLocked.compareAndSet(expect = false, update = true)) {
            logger.w { "Delete profile called while already locked" }
            return
        }

        viewModelScope.launch {
            val account =
                accountRepository.update {
                    copy(profiles = profiles.filterNot { it.id == profileId })
                }

            val currentSelection = appProfileManager.observeAppProfileId().first()
            if (currentSelection == profileId) {
                val anotherProfile = account.profiles.first()
                appProfileManager.setAppProfileId(anotherProfile.id)
            }

            _uiEventBus.send(EditProfileEvent.Deleted)
        }
    }

    private companion object {
        private const val TAG = "EditProfileViewModel"
    }
}
