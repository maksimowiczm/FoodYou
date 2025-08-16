package com.maksimowiczm.foodyou.business.settings.domain

enum class EnergyFormat {
    Kilocalories,
    Kilojoules;

    companion object {
        val DEFAULT = Kilocalories
    }
}
