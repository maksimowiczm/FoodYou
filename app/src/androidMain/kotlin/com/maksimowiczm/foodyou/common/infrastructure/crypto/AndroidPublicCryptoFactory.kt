package com.maksimowiczm.foodyou.common.infrastructure.crypto

import com.maksimowiczm.foodyou.common.crypto.SignatureVerifier
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

class AndroidSignatureVerifier : SignatureVerifier {
    override fun verify(
        publicKey: ByteArray,
        algorithm: String,
        data: ByteArray,
        signature: ByteArray,
    ): Boolean {
        return try {
            val keySpec = X509EncodedKeySpec(publicKey)
            val keyFactory = KeyFactory.getInstance(getKeyAlgorithm(algorithm))
            val pubKey = keyFactory.generatePublic(keySpec)

            val signatureInstance = Signature.getInstance(algorithm)
            signatureInstance.initVerify(pubKey)
            signatureInstance.update(data)
            signatureInstance.verify(signature)
        } catch (e: Exception) {
            false
        }
    }

    private fun getKeyAlgorithm(signatureAlgorithm: String): String =
        when {
            signatureAlgorithm.contains("RSA", ignoreCase = true) -> "RSA"
            signatureAlgorithm.contains("ECDSA", ignoreCase = true) -> "EC"
            signatureAlgorithm.contains("DSA", ignoreCase = true) -> "DSA"
            signatureAlgorithm.contains("EdDSA", ignoreCase = true) -> "EdDSA"
            else -> error("Unsupported signature algorithm: $signatureAlgorithm")
        }
}
