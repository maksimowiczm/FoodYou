package com.maksimowiczm.foodyou.common.infrastructure.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import com.maksimowiczm.foodyou.common.crypto.MasterCrypto
import java.security.KeyStore
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class AndroidMasterCrypto() : MasterCrypto {
    private val keyStore
        get() = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private val mutex = Mutex()

    override val isSupported: Flow<Boolean> = flow { emit(isSupported()) }

    private suspend fun isSupported(): Boolean {
        val key = initializeOrGetKey()
        val factory = SecretKeyFactory.getInstance(key.algorithm, ANDROID_KEYSTORE)
        val keyInfo = factory.getKeySpec(key, KeyInfo::class.java) as KeyInfo

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

    private suspend fun initializeOrGetKey(): SecretKey =
        mutex.withLock {
            val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            if (existingKey != null) {
                return@withLock existingKey
            }

            val keyGenerator = javax.crypto.KeyGenerator.getInstance("AES", ANDROID_KEYSTORE)

            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val keyGenParameterSpec =
                KeyGenParameterSpec.Builder(KEY_ALIAS, purposes)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setUserAuthenticationRequired(false)
                    .build()

            keyGenerator.init(keyGenParameterSpec)
            return@withLock keyGenerator.generateKey()
        }

    override suspend fun encrypt(data: ByteArray): ByteArray {
        val secretKey = initializeOrGetKey()

        val cipher = javax.crypto.Cipher.getInstance(TRANSFORMATION)
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encryptedData = withContext(Dispatchers.Default) { cipher.doFinal(data) }

        return iv + encryptedData
    }

    override suspend fun decrypt(encryptedData: ByteArray): ByteArray {
        val secretKey = initializeOrGetKey()
        val cipher = javax.crypto.Cipher.getInstance(TRANSFORMATION)

        val iv = encryptedData.sliceArray(0 until 12)
        val actualEncryptedData = encryptedData.sliceArray(12 until encryptedData.size)

        val spec = javax.crypto.spec.GCMParameterSpec(128, iv)
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, spec)

        return withContext(Dispatchers.Default) { cipher.doFinal(actualEncryptedData) }
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "FOOD_YOU_MASTER_KEY"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
