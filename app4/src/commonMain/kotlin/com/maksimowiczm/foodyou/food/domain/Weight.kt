package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

@JvmInline
value class Weight(val grams: Double) {
    init {
        require(grams > 0) { "Weight must be greater than zero" }
    }
}
