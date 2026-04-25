package com.maksimowiczm.foodyou.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.account.domain.update
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
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
class AppViewModel(
    private val appProfileManager: AppProfileManager,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val primaryAccount: StateFlow<Account?> =
        accountRepository
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue =
                    runBlocking {
                        accountRepository
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
                if (account == null || !account.settings.onboardingFinished) {
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
            .map { account -> account?.settings?.nutrientsOrder ?: NutrientsOrder.defaultOrder }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = NutrientsOrder.defaultOrder,
            )

    val energyUnit: StateFlow<EnergyUnit> =
        primaryAccount
            .map { it?.settings?.energyUnit ?: EnergyUnit.Kilocalories }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = EnergyUnit.Kilocalories,
            )

    fun onFinishOnboarding() {
        viewModelScope.launch {
            accountRepository.update { copy(settings = settings.copy(onboardingFinished = true)) }
        }
    }
}
