package com.maksimowiczm.foodyou.app.infrastructure.crypto

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

expect fun softwareCryptoImpl(): SoftwareCryptoImpl

class SoftwareCryptoTest {

    @Test
    fun testEncryptionDecryption() {
        val originalData = "Hello, FoodYou!".encodeToByteArray()

        val encryptedData = softwareCryptoImpl().encrypt(originalData)
        assertNotNull(encryptedData)
        assertFalse(
            originalData.contentEquals(encryptedData),
            "Encrypted data should be different from original",
        )

        val decryptedData = softwareCryptoImpl().decrypt(encryptedData)
        assertContentEquals(originalData, decryptedData, "Decrypted data should match original")
    }

    @Test
    fun testEncryptionUniqueness() {
        val originalData = "Same Data".encodeToByteArray()

        val encrypted1 = softwareCryptoImpl().encrypt(originalData)
        val encrypted2 = softwareCryptoImpl().encrypt(originalData)

        assertFalse(
            encrypted1.contentEquals(encrypted2),
            "Successive encryptions should produce different results (unique IVs)",
        )
    }
}
