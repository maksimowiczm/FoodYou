package com.maksimowiczm.foodyou.account.domain

data class Settings(val onboardingFinished: Boolean) {
    companion object {
        val default = Settings(onboardingFinished = false)
    }
}
