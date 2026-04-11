package com.maksimowiczm.foodyou.common.domain.blob

import kotlin.jvm.JvmInline

/** Stable identifier for a blob, derived from its content hash. */
@JvmInline
value class BlobDigest(val value: String) {
    init {
        require(value.isNotBlank()) { "Blob digest cannot be blank" }
    }
}
