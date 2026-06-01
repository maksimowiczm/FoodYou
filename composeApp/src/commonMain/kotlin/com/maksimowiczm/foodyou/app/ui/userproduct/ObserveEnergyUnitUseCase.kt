package com.maksimowiczm.foodyou.app.ui.userproduct

import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal fun interface ObserveEnergyUnitUseCase {
    fun observe(): Flow<EnergyUnit>

    suspend fun get() = observe().first()
}

internal class ObserveEnergyUnitUseCaseImpl(private val accountService: AccountService) :
    ObserveEnergyUnitUseCase {
    override fun observe(): Flow<EnergyUnit> =
        accountService.observe().filterNotNull().map { it.energyUnit }
}
