package com.maksimowiczm.foodyou.app.business.opensource.domain.settings

enum class EnergyFormat {
    Kilocalories,
    Kilojoules;

    companion object {
        val DEFAULT = Kilocalories
    }
}
