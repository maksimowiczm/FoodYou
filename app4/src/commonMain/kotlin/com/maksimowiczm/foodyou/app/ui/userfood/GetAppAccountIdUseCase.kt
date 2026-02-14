package com.maksimowiczm.foodyou.app.ui.userfood

import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import kotlinx.coroutines.flow.first

fun interface GetAppAccountEnergyFormatUseCase {
    suspend fun getAppAccountEnergyFormat(): EnergyFormat
}

class GetAppAccountEnergyFormatUseCaseImpl(private val appAccountManager: AppAccountManager) :
    GetAppAccountEnergyFormatUseCase {
    override suspend fun getAppAccountEnergyFormat(): EnergyFormat {
        val account = appAccountManager.observeAppAccount().first()
        return account.settings.energyFormat
    }
}
