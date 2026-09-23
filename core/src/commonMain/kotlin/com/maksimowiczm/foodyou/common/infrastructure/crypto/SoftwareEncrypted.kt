package com.maksimowiczm.foodyou.common.infrastructure.crypto

import kotlin.jvm.JvmInline

@JvmInline
value class SoftwareEncrypted(val data: ByteArray) {
    fun decrypt(): ByteArray = SoftwareCrypto.decrypt(data)

    companion object {
        fun encrypt(data: ByteArray) = SoftwareEncrypted(SoftwareCrypto.encrypt(data))
    }
}

fun SoftwareEncrypted.decryptString() = decrypt().decodeToString()

fun SoftwareEncrypted.Companion.encryptString(data: String) = encrypt(data.encodeToByteArray())
