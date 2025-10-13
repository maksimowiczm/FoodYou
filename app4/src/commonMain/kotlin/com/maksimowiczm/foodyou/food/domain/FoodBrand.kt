package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

@JvmInline
value class FoodBrand(val value: String) {
    init {
        require(value.isNotBlank()) { "Brand cannot be blank" }
    }
}
