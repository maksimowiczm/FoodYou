package com.maksimowiczm.foodyou.preferences.domain

import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import kotlin.jvm.JvmInline

@JvmInline
value class NutrientsOrderPreference(val nutrientsOrder: List<NutrientsOrder>) : UserPreferences {
    init {
        require(nutrientsOrder.toSet().size == NutrientsOrder.entries.size) {
            "Nutrients order must contain all nutrients"
        }
    }
}
