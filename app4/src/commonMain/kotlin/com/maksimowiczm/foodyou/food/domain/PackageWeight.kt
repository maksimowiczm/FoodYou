package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

@JvmInline
value class PackageWeight(val grams: Int) {
    init {
        require(grams > 0) { "Package weight must be greater than zero" }
    }
}
