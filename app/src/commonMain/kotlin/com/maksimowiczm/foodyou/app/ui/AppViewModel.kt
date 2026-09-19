package com.maksimowiczm.foodyou.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.preferences.domain.EnergyUnitPreference
import com.maksimowiczm.foodyou.preferences.domain.NutrientsOrderPreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    private val accountService: AccountService,
    preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val appPage: StateFlow<AppPage> =
        accountService
            .observe()
            .map { account ->
                if (account == null || !account.onboardingFinished) AppPage.Onboarding
                else AppPage.Main
            }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = AppPage.Splash,
            )

    val nutrientsOrder: StateFlow<List<NutrientsOrder>> =
        preferencesRepository
            .observe<NutrientsOrderPreference>()
            .map { it.nutrientsOrder }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = NutrientsOrder.defaultOrder,
            )

    val energyUnit: StateFlow<EnergyUnit> =
        preferencesRepository
            .observe<EnergyUnitPreference>()
            .map { it.energyUnit }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = EnergyUnit.Kilocalories,
            )

    fun onFinishOnboarding() {
        viewModelScope.launch {
            accountService.handle(AccountCommand.FinishOnboarding)
        }
    }
}
