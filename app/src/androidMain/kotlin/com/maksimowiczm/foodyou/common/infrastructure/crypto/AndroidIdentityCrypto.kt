package com.maksimowiczm.foodyou.common.infrastructure.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import com.maksimowiczm.foodyou.common.crypto.IdentityCrypto
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AndroidIdentityCrypto : IdentityCrypto {
    private val keyStore
        get() = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private val mutex = Mutex()

    override val isSupported: Flow<Boolean> = flow { emit(isSupported()) }
    override val algorithm: String = ALGORITHM
    override val publicKey: ByteArray by lazy {
        val key = runBlocking { initializeOrGetKey() }

        if (key.certificate.publicKey.format != "X.509") {
            error("Public key format is not X.509")
        }

        key.certificate.publicKey.encoded
    }

    private suspend fun isSupported(): Boolean {
        val key = initializeOrGetKey()
        val factory = KeyFactory.getInstance(key.privateKey.algorithm, ANDROID_KEYSTORE)
        val keyInfo = factory.getKeySpec(key.privateKey, KeyInfo::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val securityLevel = keyInfo.securityLevel
            securityLevel == KeyProperties.SECURITY_LEVEL_STRONGBOX ||
                securityLevel == KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT
        } else
            @Suppress("DEPRECATION")
            {
                keyInfo.isInsideSecureHardware
            }
    }

    private suspend fun initializeOrGetKey(): KeyStore.PrivateKeyEntry =
        mutex.withLock {
            val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
            if (existingKey != null) {
                return@withLock existingKey
            }

            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEYSTORE)

            val spec =
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_SIGN)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setUserAuthenticationRequired(false)
                    .build()

            kpg.initialize(spec)
            kpg.genKeyPair()

            return@withLock keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        }

    override suspend fun sign(data: ByteArray): ByteArray {
        val key = initializeOrGetKey()
        val signature = java.security.Signature.getInstance(ALGORITHM)
        signature.initSign(key.privateKey)
        signature.update(data)

        return withContext(Dispatchers.Default) { signature.sign() }
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "FOODYOU_IDENTITY_KEY"
        const val ALGORITHM = "SHA256withECDSA"
    }
}
