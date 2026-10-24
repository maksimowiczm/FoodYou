package com.maksimowiczm.foodyou.common.domain.crypto

/** A fast, software-based symmetric cipher. */
interface SoftwareCrypto {
    fun encrypt(data: ByteArray): ByteArray

    fun decrypt(data: ByteArray): ByteArray
}
