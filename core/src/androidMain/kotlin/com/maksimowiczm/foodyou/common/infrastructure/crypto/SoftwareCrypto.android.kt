package com.maksimowiczm.foodyou.common.infrastructure.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android implementation of [SoftwareCrypto].
 *
 * Uses AES-256-GCM via the Android KeyStore system without StrongBox backing. Key operations are
 * performed in the TEE (Trusted Execution Environment) or software.
 *
 * The IV is prepended to the ciphertext and extracted automatically on decryption.
 */
actual object SoftwareCrypto {
    private const val KEY_ALIAS = "FoodYouSoftwareCryptoKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val IV_SIZE = 12
    private const val TAG_LENGTH = 128
    private const val MIN_CIPHERTEXT_SIZE = IV_SIZE + TAG_LENGTH / 8

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    private fun KeyStore.getOrCreateKey(): SecretKey {
        if (containsAlias(KEY_ALIAS)) {
            return (getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }

        val spec =
            KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                .setKeySize(KEY_SIZE)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()

        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            .apply { init(spec) }
            .generateKey()
    }

    actual fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keyStore.getOrCreateKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(data)
        return iv + ciphertext
    }

    actual fun decrypt(data: ByteArray): ByteArray {
        require(data.size >= MIN_CIPHERTEXT_SIZE) {
            "Invalid data: too short to contain IV and GCM tag"
        }

        val iv = data.sliceArray(0 until IV_SIZE)
        val ciphertext = data.sliceArray(IV_SIZE until data.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            keyStore.getOrCreateKey(),
            GCMParameterSpec(TAG_LENGTH, iv),
        )

        return cipher.doFinal(ciphertext)
    }
}
