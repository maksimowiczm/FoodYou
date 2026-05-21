package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettings
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class FoodDataCentralSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : FoodDataCentralSettingsRepository {
    override fun observe(): Flow<FoodDataCentralSettings> =
        dataStore.data.map {
            FoodDataCentralSettings(
                remoteEnabled = it[remoteEnabled] ?: false,
                apiKey = it[usdaApiKey]?.ifBlank { null },
            )
        }

    override suspend fun save(settings: FoodDataCentralSettings) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[remoteEnabled] = settings.remoteEnabled
                this[usdaApiKey] = settings.apiKey ?: ""
            }
        }
    }

    override suspend fun update(transform: (FoodDataCentralSettings) -> FoodDataCentralSettings) {
        save(transform(observe().first()))
    }

    private companion object {
        private val remoteEnabled = booleanPreferencesKey("fooddatacentral:remoteEnabled")
        private val usdaApiKey = stringPreferencesKey("fooddatacentral:usda_api_key")
    }
}
