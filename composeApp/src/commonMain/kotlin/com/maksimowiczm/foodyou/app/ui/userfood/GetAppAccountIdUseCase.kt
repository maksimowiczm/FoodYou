package com.maksimowiczm.foodyou.app.ui.userfood

import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import kotlinx.coroutines.flow.first

internal fun interface GetAppAccountEnergyUnitUseCase {
    suspend fun getAppAccountEnergyUnit(): EnergyUnit
}

internal class GetAppAccountEnergyUnitUseCaseImpl(
    private val appAccountManager: AppAccountManager
) : GetAppAccountEnergyUnitUseCase {
    override suspend fun getAppAccountEnergyUnit(): EnergyUnit {
        val account = appAccountManager.observeAppAccount().first()
        return account.settings.energyUnit
    }
}
