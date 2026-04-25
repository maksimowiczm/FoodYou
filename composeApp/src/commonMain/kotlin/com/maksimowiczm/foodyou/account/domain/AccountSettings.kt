package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.EnergyUnit

data class AccountSettings(
    val onboardingFinished: Boolean = false,
    val energyUnit: EnergyUnit = EnergyUnit.Kilocalories,
    val nutrientsOrder: List<NutrientsOrder> = NutrientsOrder.defaultOrder,
)
