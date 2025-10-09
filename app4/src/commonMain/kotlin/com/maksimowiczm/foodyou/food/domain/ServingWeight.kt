package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

@JvmInline
value class ServingWeight(val grams: Int) {
    init {
        require(grams > 0) { "Serving weight must be greater than zero" }
    }
}
