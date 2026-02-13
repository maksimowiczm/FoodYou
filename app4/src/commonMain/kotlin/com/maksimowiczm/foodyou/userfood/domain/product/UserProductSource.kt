package com.maksimowiczm.foodyou.userfood.domain.product

import kotlin.jvm.JvmInline

@JvmInline
value class UserProductSource(val value: String) {
    init {
        require(value.isNotBlank()) { "Food source cannot be blank" }
    }
}
