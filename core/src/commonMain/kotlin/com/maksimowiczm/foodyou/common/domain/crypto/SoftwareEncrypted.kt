package com.maksimowiczm.foodyou.common.domain.crypto

data class SoftwareEncrypted(val data: ByteArray) {
    fun decrypt(crypto: SoftwareCrypto): ByteArray = crypto.decrypt(data)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SoftwareEncrypted) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String = "SoftwareEncrypted(data=${data.size} bytes)"

    companion object {
        fun encrypt(data: ByteArray, crypto: SoftwareCrypto) =
            SoftwareEncrypted(crypto.encrypt(data))
    }
}

fun SoftwareEncrypted.decryptString(crypto: SoftwareCrypto) = decrypt(crypto).decodeToString()

fun SoftwareEncrypted.Companion.encryptString(data: String, crypto: SoftwareCrypto) =
    encrypt(data.encodeToByteArray(), crypto)
