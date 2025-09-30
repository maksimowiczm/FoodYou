package com.maksimowiczm.foodyou.common.crypto

import kotlinx.coroutines.flow.Flow

/**
 * Interface for authentication. Implementation provides hardware-backed security. Private key CAN
 * NOT be exported from the secure hardware. This is unique key for the device.
 */
interface IdentityCrypto {
    /** True if the device supports hardware-backed key storage and operations */
    val isSupported: Flow<Boolean>

    /** Algorithm used for signing, e.g. "SHA256withECDSA" */
    val algorithm: String

    /** Public key in X.509 format (DER encoded) */
    val publicKey: ByteArray

    /**
     * Sign the data with the private key stored in secure hardware
     *
     * @param data Data to sign
     * @return Signature
     */
    suspend fun sign(data: ByteArray): ByteArray
}
