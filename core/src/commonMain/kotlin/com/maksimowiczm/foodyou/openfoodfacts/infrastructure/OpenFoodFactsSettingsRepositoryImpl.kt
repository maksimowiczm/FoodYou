package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.common.extension.set
import com.maksimowiczm.foodyou.common.infrastructure.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsCredentials
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsSettingsRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    OpenFoodFactsSettingsRepository {

    override fun observe(): Flow<OpenFoodFactsSettings> {
        return dataStore.data.map { preferences ->
            val login = preferences[login]
            val password = preferences[password]
            val credentials =
                if (login != null && password != null)
                    OpenFoodFactsCredentials(
                        login = SoftwareEncrypted(login),
                        password = SoftwareEncrypted(password),
                    )
                else null

            OpenFoodFactsSettings(
                remoteEnabled = preferences[remoteEnabled] ?: false,
                credentials = credentials,
            )
        }
    }

    override suspend fun save(settings: OpenFoodFactsSettings) {
        dataStore.edit {
            it[remoteEnabled] = settings.remoteEnabled
            it[login] = settings.credentials?.login?.data
            it[password] = settings.credentials?.password?.data
        }
    }

    override suspend fun update(transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings) {
        save(transform(observe().first()))
    }

    private companion object {
        val remoteEnabled = booleanPreferencesKey("openfoodfacts:remoteEnabled")
        val login = byteArrayPreferencesKey("openfoodfacts:login")
        val password = byteArrayPreferencesKey("openfoodfacts:password")
    }
}
