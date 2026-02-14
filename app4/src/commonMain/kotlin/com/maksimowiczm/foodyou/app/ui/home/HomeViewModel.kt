package com.maksimowiczm.foodyou.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val appAccountManager: AppAccountManager,
    private val accountRepository: AccountRepository,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag(TAG)

    private val profile =
        appAccountManager
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
            val profileId = appAccountManager.observeAppProfileId().filterNotNull().first()
            val account = appAccountManager.observeAppAccount().first()

            account.updateProfile(profileId) { it.apply { updateHomeCardsOrder(newOrder) } }

            accountRepository.save(account)

            logger.d { "Reordered home features: $newOrder" }
        }
    }

    private companion object {
        private const val TAG = "HomeViewModel"
    }
}
