package com.maksimowiczm.foodyou.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AppViewModel(
    private val accountRepository: AccountRepository,
    private val accountManager: AccountManager,
) : ViewModel() {
    private val primaryAccountId =
        accountManager
            .observePrimaryAccountId()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { accountManager.observePrimaryAccountId().first() },
            )

    val onboardingFinished =
        primaryAccountId
            .flatMapLatest { id ->
                if (id == null) {
                    return@flatMapLatest flowOf(false)
                }

                accountRepository.observe(id).map { it?.settings?.onboardingFinished ?: false }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun onFinishOnboarding(localAccountId: LocalAccountId) {
        viewModelScope.launch {
            val account = accountRepository.load(localAccountId) ?: return@launch

            account.updateSettings { it.copy(onboardingFinished = true) }

            accountRepository.save(account)
        }
    }
}
