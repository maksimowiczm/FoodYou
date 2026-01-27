package com.maksimowiczm.foodyou.common.domain

import kotlin.jvm.JvmInline

@JvmInline
value class FoodNote(val value: String) {
    init {
        require(value.isNotBlank()) { "Note cannot be blank" }
    }
}
