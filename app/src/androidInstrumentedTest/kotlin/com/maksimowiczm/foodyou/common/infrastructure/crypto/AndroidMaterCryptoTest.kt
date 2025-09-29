package com.maksimowiczm.foodyou.common.infrastructure.crypto

import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AndroidMaterCryptoTest {
    // This test doesn't really test anything other than if the device supports MasterCrypto which
    // depends on test runner device
    @Test
    fun encrypt_decrypt() = runBlocking {
        val crypto = AndroidMasterCrypto()

        assert(crypto.isSupported.first()) { "MasterCrypto is not supported on this device" }

        val originalData = "Hello, World!".toByteArray()
        val encryptedData = crypto.encrypt(originalData)
        val decryptedData = crypto.decrypt(encryptedData)

        assert(originalData.contentEquals(decryptedData)) {
            "Decrypted data does not match original"
        }
    }
}
