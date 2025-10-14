package com.maksimowiczm.foodyou.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AppViewModel(
    private val accountRepository: AccountRepository,
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
) : ViewModel() {
    private val primaryAccount =
        observePrimaryAccountUseCase
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { observePrimaryAccountUseCase.observe().first() },
            )

    val onboardingFinished =
        primaryAccount
            .map { account -> account?.settings?.onboardingFinished ?: false }
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
