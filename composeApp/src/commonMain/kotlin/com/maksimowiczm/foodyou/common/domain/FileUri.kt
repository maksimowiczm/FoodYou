package com.maksimowiczm.foodyou.common.domain

import kotlin.jvm.JvmInline

@JvmInline
value class FileUri(val value: String) {
    init {
        require(value.isNotBlank()) { "ImageUri cannot be blank" }
    }
}
