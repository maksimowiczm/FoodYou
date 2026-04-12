package com.maksimowiczm.foodyou.common.domain

import kotlin.jvm.JvmInline

/**
 * A URI that unambiguously identifies an image — whether it is a remote URL (https://…) or a local
 * content/file URI. Consumers treat both the same way.
 */
@JvmInline
value class ImageUri(val value: String) {
    init {
        require(value.isNotBlank()) { "ImageUri cannot be blank" }
    }
}
