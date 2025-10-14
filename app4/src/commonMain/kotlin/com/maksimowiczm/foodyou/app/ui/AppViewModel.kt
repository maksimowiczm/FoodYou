package com.maksimowiczm.foodyou.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.utility.EnergyFormatter
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AppViewModel(
    private val accountRepository: AccountRepository,
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
) : ViewModel() {
    private val primaryAccount: StateFlow<Account?> =
        observePrimaryAccountUseCase
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { observePrimaryAccountUseCase.observe().first() },
            )

    val onboardingFinished: StateFlow<Boolean?> =
        primaryAccount
            .map { account -> account?.settings?.onboardingFinished ?: false }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val nutrientsOrder: StateFlow<List<NutrientsOrder>> =
        primaryAccount
            .map { account -> account?.settings?.nutrientsOrder ?: NutrientsOrder.defaultOrder }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = NutrientsOrder.defaultOrder,
            )

    val energyFormatter: StateFlow<EnergyFormatter> =
        primaryAccount
            .map { account ->
                when (account?.settings?.energyFormat) {
                    EnergyFormat.Kilocalories -> EnergyFormatter.kilocalories
                    EnergyFormat.Kilojoules -> EnergyFormatter.kilojoules
                    null -> EnergyFormatter.kilocalories
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = EnergyFormatter.kilocalories,
            )

    fun onFinishOnboarding(localAccountId: LocalAccountId) {
        viewModelScope.launch {
            val account = accountRepository.load(localAccountId) ?: return@launch

            account.updateSettings { it.copy(onboardingFinished = true) }

            accountRepository.save(account)
        }
    }
}
