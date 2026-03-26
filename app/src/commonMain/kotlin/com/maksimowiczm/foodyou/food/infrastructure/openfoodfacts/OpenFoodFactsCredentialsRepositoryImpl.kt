package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.common.crypto.MasterCrypto
import com.maksimowiczm.foodyou.food.domain.repository.OpenFoodFactsCredentialsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsCredentialsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val masterCrypto: MasterCrypto,
) : OpenFoodFactsCredentialsRepository {
    override suspend fun store(login: String, password: String) {
        dataStore.edit {
            it[loginKey] = masterCrypto.encrypt(login.encodeToByteArray())
            it[passwordKey] = masterCrypto.encrypt(password.encodeToByteArray())
        }
    }

    override suspend fun clear() {
        dataStore.edit {
            it.remove(loginKey)
            it.remove(passwordKey)
        }
    }

    override fun hasCredentials(): Flow<Boolean> =
        dataStore.data.map { loginKey in it && passwordKey in it }

    suspend fun loadCredentials(): Pair<String, String>? {
        val preferences = dataStore.data.first()

        return if (loginKey in preferences && passwordKey in preferences) {
            val encryptedLogin = preferences[loginKey] ?: return null
            val encryptedPassword = preferences[passwordKey] ?: return null

            val login = masterCrypto.decrypt(encryptedLogin).decodeToString()
            val password = masterCrypto.decrypt(encryptedPassword).decodeToString()

            login to password
        } else {
            null
        }
    }

    private companion object {
        private val loginKey = byteArrayPreferencesKey("openFoodFacts:login")
        private val passwordKey = byteArrayPreferencesKey("openFoodFacts:password")
    }
}
