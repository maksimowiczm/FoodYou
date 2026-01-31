package com.maksimowiczm.foodyou.userfood.domain

import kotlin.jvm.JvmInline

@JvmInline
value class FoodSource(val value: String) {
    init {
        require(value.isNotBlank()) { "Food source cannot be blank" }
    }
}
