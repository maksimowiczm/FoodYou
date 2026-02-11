package com.maksimowiczm.foodyou.userfood.domain.product

import kotlin.jvm.JvmInline

@JvmInline
value class FoodSource(val value: String) {
    init {
        require(value.isNotBlank()) { "Food source cannot be blank" }
    }
}
