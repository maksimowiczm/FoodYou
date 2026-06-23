package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.extension.set
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsSettingsRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    OpenFoodFactsSettingsRepository {

    override fun observe(): Flow<OpenFoodFactsSettings> {
        return dataStore.data.map { preferences ->
            OpenFoodFactsSettings(
                remoteEnabled = preferences[remoteEnabled] ?: false,
                login = preferences[login],
                password = preferences[password],
            )
        }
    }

    override suspend fun save(settings: OpenFoodFactsSettings) {
        dataStore.edit {
            it[remoteEnabled] = settings.remoteEnabled
            it[login] = settings.login
            it[password] = settings.password
        }
    }

    override suspend fun update(transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings) {
        save(transform(observe().first()))
    }

    private companion object {
        val remoteEnabled = booleanPreferencesKey("openfoodfacts:remoteEnabled")
        val login = stringPreferencesKey("openfoodfacts:login")
        val password = stringPreferencesKey("openfoodfacts:password")
    }
}
