package com.maksimowiczm.foodyou.userfood.domain

import kotlin.jvm.JvmInline

@JvmInline
value class UserFoodNote(val value: String) {
    init {
        require(value.isNotBlank()) { "Note cannot be blank" }
    }
}
