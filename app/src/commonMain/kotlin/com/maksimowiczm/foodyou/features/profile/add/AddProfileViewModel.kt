package com.maksimowiczm.foodyou.features.profile.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.shared.ui.component.UiProfileAvatar
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class AddProfileViewModel(
    private val appProfileManager: AppProfileManager,
    private val accountService: AccountService,
    private val blobStorage: BlobStorage,
) : ViewModel() {

    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(false)

    private val _uiEventBus = Channel<AddProfileEvent>()
    val uiEvents = _uiEventBus.receiveAsFlow()

    fun create(name: String, avatar: UiProfileAvatar) {
        if (!isLocked.compareAndSet(expect = false, update = true)) {
            return
        }

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

            val profile = Profile(name = name, avatar = profileAvatar)
            accountService.handle(AccountCommand.AddProfile(profile))

            appProfileManager.setAppProfileId(profile.id)

            _uiEventBus.send(AddProfileEvent.Created(profile.id))
        }
    }
}
