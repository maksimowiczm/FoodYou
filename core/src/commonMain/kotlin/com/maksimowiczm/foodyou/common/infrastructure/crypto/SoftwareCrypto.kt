package com.maksimowiczm.foodyou.common.infrastructure.crypto

/**
 * A fast, software-based symmetric cipher.
 *
 * Key management and algorithm details are encapsulated within the platform-specific
 * implementation.
 */
expect object SoftwareCrypto {
    fun encrypt(data: ByteArray): ByteArray

    fun decrypt(data: ByteArray): ByteArray
}
