package com.maksimowiczm.foodyou.features.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.domain.removeProfile
import com.maksimowiczm.foodyou.account.domain.updateProfile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.shared.ui.component.UiProfileAvatar
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
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
    private val accountService: AccountService,
    private val blobStorage: BlobStorage,
) : ViewModel() {
    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val canDelete: StateFlow<Boolean> =
        accountService
            .observe()
            .filterNotNull()
            .map { it.profiles.size > 1 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = false,
            )

    val profile: StateFlow<Profile?> =
        accountService
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
            return
        }

        require(name.isNotBlank()) { "Name cannot be blank" }

        viewModelScope.launch {
            val profileAvatar =
                when (avatar) {
                    is UiProfileAvatar.Predefined -> Profile.Avatar.Predefined(avatar.variant)
                    is UiProfileAvatar.Uri -> {
                        val file = PlatformFile(avatar.uri.value)
                        val digest = blobStorage.store(file.readBytes())
                        Profile.Avatar.Photo(digest)
                    }
                }

            val profileId = profile.filterNotNull().first().id
            accountService.update {
                updateProfile(profileId) { it.copy(name = name, avatar = profileAvatar) }
            }

            _uiEventBus.send(EditProfileEvent.Edited)
        }
    }

    fun delete() {
        if (!isLocked.compareAndSet(expect = false, update = true)) {
            return
        }

        viewModelScope.launch {
            accountService.update { removeProfile(profileId) }

            val currentSelection = appProfileManager.observeAppProfileId().first()
            if (currentSelection == profileId) {
                val account = accountService.observe().filterNotNull().first()
                val anotherProfile = account.profiles.first()
                appProfileManager.setAppProfileId(anotherProfile.id)
            }

            _uiEventBus.send(EditProfileEvent.Deleted)
        }
    }
}
