package com.maksimowiczm.foodyou.common.crypto

import kotlinx.coroutines.flow.Flow

/**
 * Interface for master encryption and decryption. Implementation provides hardware-backed security.
 * Master key CAN NOT be exported from the secure hardware. This is unique key for the device.
 */
interface MasterCrypto {

    val isSupported: Flow<Boolean>

    suspend fun encrypt(data: ByteArray): ByteArray

    suspend fun decrypt(encryptedData: ByteArray): ByteArray
}
