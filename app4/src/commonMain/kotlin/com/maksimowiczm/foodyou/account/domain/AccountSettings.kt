package com.maksimowiczm.foodyou.account.domain

data class AccountSettings(val onboardingFinished: Boolean, val energyFormat: EnergyFormat) {
    companion object {
        val default =
            AccountSettings(onboardingFinished = false, energyFormat = EnergyFormat.Kilocalories)
    }
}
