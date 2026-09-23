package com.maksimowiczm.foodyou.common.domain

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class FileUri(val value: String) {
    init {
        require(value.isNotBlank()) { "FileUri cannot be blank" }
    }
}
