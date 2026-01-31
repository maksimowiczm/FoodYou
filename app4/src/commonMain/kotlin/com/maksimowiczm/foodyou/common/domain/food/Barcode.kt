package com.maksimowiczm.foodyou.common.domain.food

import kotlin.jvm.JvmInline

@JvmInline
value class Barcode(val value: String) {
    init {
        require(value.isNotBlank()) { "Barcode cannot be blank" }
        require(value.all { it.isDigit() }) { "Barcode must contain only digits" }
    }
}
