package com.maksimowiczm.foodyou.common.infrastructure.crypto

import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AndroidIdentityCryptoTest {
    @Test
    fun sign_verify() = runBlocking {
        // This test doesn't really test anything other than if the device supports IdentityCrypto
        // which
        // depends on test runner device
        val crypto = AndroidIdentityCrypto()
        val verifier = AndroidSignatureVerifier()

        assert(crypto.isSupported.first()) { "MasterCrypto is not supported on this device" }

        val originalData = "Hello, World!".toByteArray()
        val signedData = crypto.sign(originalData)
        val isSignatureValid =
            verifier.verify(
                publicKey = crypto.publicKey,
                algorithm = crypto.algorithm,
                data = originalData,
                signature = signedData,
            )

        assert(isSignatureValid) { "Signature verification failed" }
    }
}
