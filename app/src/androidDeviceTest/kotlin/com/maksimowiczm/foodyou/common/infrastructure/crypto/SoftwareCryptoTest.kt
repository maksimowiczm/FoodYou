package com.maksimowiczm.foodyou.common.infrastructure.crypto

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class SoftwareCryptoTest {

    @Test
    fun testEncryptionDecryption() {
        val originalData = "Hello, FoodYou!".encodeToByteArray()

        val encryptedData = SoftwareCrypto.encrypt(originalData)
        assertNotNull(encryptedData)
        assertFalse(
            originalData.contentEquals(encryptedData),
            "Encrypted data should be different from original",
        )

        val decryptedData = SoftwareCrypto.decrypt(encryptedData)
        assertContentEquals(originalData, decryptedData, "Decrypted data should match original")
    }

    @Test
    fun testEncryptionUniqueness() {
        val originalData = "Same Data".encodeToByteArray()

        val encrypted1 = SoftwareCrypto.encrypt(originalData)
        val encrypted2 = SoftwareCrypto.encrypt(originalData)

        assertFalse(
            encrypted1.contentEquals(encrypted2),
            "Successive encryptions should produce different results (unique IVs)",
        )
    }
}
