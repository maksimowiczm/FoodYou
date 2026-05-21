package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsSettingsRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    OpenFoodFactsSettingsRepository {

    override fun observe(): Flow<OpenFoodFactsSettings> {
        return dataStore.data.map { preferences ->
            OpenFoodFactsSettings(remoteEnabled = preferences[remoteEnabled] ?: false)
        }
    }

    override suspend fun save(settings: OpenFoodFactsSettings) {
        dataStore.edit { it[remoteEnabled] = settings.remoteEnabled }
    }

    override suspend fun update(transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings) {
        save(transform(observe().first()))
    }

    private companion object {
        val remoteEnabled = booleanPreferencesKey("openfoodfacts:remoteEnabled")
    }
}
