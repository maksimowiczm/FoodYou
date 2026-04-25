package com.maksimowiczm.foodyou.app.ui.profile.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.account.domain.update
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.app.ui.common.component.ProfileAvatarMapper
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class AddProfileViewModel(
    private val appProfileManager: AppProfileManager,
    private val accountRepository: AccountRepository,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag(TAG)

    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(false)

    private val _uiEventBus = Channel<AddProfileEvent>()
    val uiEvents = _uiEventBus.receiveAsFlow()

    fun create(name: String, avatar: UiProfileAvatar) {
        if (!isLocked.compareAndSet(expect = false, update = true)) {
            logger.w { "Create profile called while already locked" }
            return
        }

        viewModelScope.launch {
            logger.d { "Loading primary account to add profile" }

            val profile = Profile(name = name, avatar = ProfileAvatarMapper.toModel(avatar))
            accountRepository.update { copy(profiles = profiles + profile) }

            appProfileManager.setAppProfileId(profile.id)

            _uiEventBus.send(AddProfileEvent.Created(profile.id))
        }
    }

    private companion object {
        private const val TAG = "AddProfileViewModel"
    }
}
