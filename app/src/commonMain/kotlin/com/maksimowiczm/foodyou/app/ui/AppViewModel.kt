package com.maksimowiczm.foodyou.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(FlowPreview::class)
class AppViewModel(private val accountService: AccountService) : ViewModel() {
    private val primaryAccount: StateFlow<Account?> =
        accountService
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue =
                    runBlocking {
                        accountService
                            .observe()
                            .timeout(1.seconds)
                            .catch {
                                when (it) {
                                    is TimeoutCancellationException -> emit(null)
                                    else -> throw it
                                }
                            }
                            .first()
                    },
            )

    val appPage: StateFlow<AppPage> =
        primaryAccount
            .map { account ->
                if (account == null || !account.onboardingFinished) {
                    AppPage.Onboarding
                } else {
                    AppPage.Main
                }
            }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = AppPage.Splash,
            )

    val nutrientsOrder: StateFlow<List<NutrientsOrder>> =
        primaryAccount
            .map { account -> account?.nutrientsOrder ?: NutrientsOrder.defaultOrder }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = NutrientsOrder.defaultOrder,
            )

    val energyUnit: StateFlow<EnergyUnit> =
        primaryAccount
            .map { it?.energyUnit ?: EnergyUnit.Kilocalories }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = EnergyUnit.Kilocalories,
            )

    fun onFinishOnboarding() {
        viewModelScope.launch {
            accountService.handle(AccountCommand.FinishOnboarding(Clock.System.now()))
        }
    }
}
