package com.maksimowiczm.foodyou.userfood.domain.product

import kotlin.jvm.JvmInline

@JvmInline
value class UserProductBarcode(val value: String) {
    init {
        require(value.isNotBlank()) { "Barcode cannot be blank" }
        require(value.all { it.isDigit() }) { "Barcode must contain only digits" }
    }
}
