package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.EnergyUnit

data class AccountSettings(
    val onboardingFinished: Boolean,
    val energyUnit: EnergyUnit,
    val nutrientsOrder: List<NutrientsOrder>,
) {
    companion object {
        val default =
            AccountSettings(
                onboardingFinished = false,
                energyUnit = EnergyUnit.Kilocalories,
                nutrientsOrder = NutrientsOrder.defaultOrder,
            )
    }
}
