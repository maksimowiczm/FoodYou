package com.maksimowiczm.foodyou.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.HomeCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val accountManager: AccountManager,
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val accountRepository: AccountRepository,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag(TAG)

    private val profile =
        accountManager
            .observePrimaryProfileId()
            .flatMapLatest { profileId ->
                observePrimaryAccountUseCase.observe().map { account ->
                    account.profiles.find { it.id == profileId }
                }
            }
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
            val profileId = accountManager.observePrimaryProfileId().first()
            val account = observePrimaryAccountUseCase.observe().first()

            account.updateProfile(profileId) { it.apply { updateHomeCardsOrder(newOrder) } }

            accountRepository.save(account)

            logger.d { "Reordered home features: $newOrder" }
        }
    }

    private companion object {
        private const val TAG = "HomeViewModel"
    }
}
