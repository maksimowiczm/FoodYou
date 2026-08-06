package com.maksimowiczm.foodyou.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.account.domain.updateProfile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class HomeOrderViewModel(
    private val appProfileManager: AppProfileManager,
    private val accountService: AccountService,
) : ViewModel() {
    private val profile =
        appProfileManager
            .observeAppProfile()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    private val _homeOrder = profile.filterNotNull().map { it.homeCardsOrder }

    val homeOrder =
        _homeOrder.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = emptyList(),
        )

    fun reorder(newOrder: List<HomeCard>) {
        viewModelScope.launch {
            val profileId = appProfileManager.observeAppProfileId().filterNotNull().first()
            accountService.update {
                updateProfile(profileId) { it.copy(homeCardsOrder = newOrder) }
            }
        }
    }
}
