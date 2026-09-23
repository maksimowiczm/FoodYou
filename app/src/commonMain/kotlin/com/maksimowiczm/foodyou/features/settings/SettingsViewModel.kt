package com.maksimowiczm.foodyou.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appProfileManager: AppProfileManager,
    accountService: AccountService,
) : ViewModel() {
    val profiles =
        accountService
            .observe()
            .filterNotNull()
            .map { it.profiles }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = null,
            )

    val selectedProfile =
        appProfileManager
            .observeAppProfileId()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = null,
            )

    fun selectProfile(profileId: ProfileId) {
        viewModelScope.launch { appProfileManager.setAppProfileId(profileId) }
    }
}
