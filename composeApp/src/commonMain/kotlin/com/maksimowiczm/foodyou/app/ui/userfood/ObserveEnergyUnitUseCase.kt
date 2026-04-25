package com.maksimowiczm.foodyou.app.ui.userfood

import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal fun interface ObserveEnergyUnitUseCase {
    fun observe(): Flow<EnergyUnit>

    suspend fun get() = observe().first()
}

internal class ObserveEnergyUnitUseCaseImpl(private val accountRepository: AccountRepository) :
    ObserveEnergyUnitUseCase {
    override fun observe(): Flow<EnergyUnit> =
        accountRepository.observe().filterNotNull().map { it.settings.energyUnit }
}
