package com.maksimowiczm.foodyou.common.crypto

fun interface SignatureVerifier {

    /**
     * Verifies the signature of the given data using the provided public key.
     *
     * @param publicKey The public key used for verification. Must be in X.509 format.
     * @param algorithm The algorithm used for verification (e.g., "SHA256withRSA").
     * @param data The original data that was signed.
     * @param signature The signature to verify.
     * @return True if the signature is valid, false otherwise.
     */
    fun verify(
        publicKey: ByteArray,
        algorithm: String,
        data: ByteArray,
        signature: ByteArray,
    ): Boolean
}
