package com.maksimowiczm.foodyou.settings.domain.entity

enum class EnergyFormat {
    Kilocalories,
    Kilojoules;

    companion object {
        val DEFAULT = Kilocalories
    }
}
