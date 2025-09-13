package com.maksimowiczm.foodyou.app.business.shared.domain.settings

enum class EnergyFormat {
    Kilocalories,
    Kilojoules;

    companion object {
        val DEFAULT = Kilocalories
    }
}
