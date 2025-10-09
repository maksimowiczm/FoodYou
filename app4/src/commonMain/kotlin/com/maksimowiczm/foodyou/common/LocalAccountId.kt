package com.maksimowiczm.foodyou.common

import kotlin.jvm.JvmInline

@JvmInline
value class LocalAccountId(val value: String) {
    companion object {
        /**
         * Currently app supports only one local account. Use this default ID for that account, so
         * other parts of the code can refer to it.
         */
        val DEFAULT = LocalAccountId("LocalAccountId.DEFAULT")
    }
}
