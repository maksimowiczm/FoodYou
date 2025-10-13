package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

@JvmInline
value class FoodNote(val value: String) {
    init {
        require(value.isNotBlank()) { "Note cannot be blank" }
    }
}
