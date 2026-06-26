package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.common.extension.set
import com.maksimowiczm.foodyou.common.infrastructure.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettings
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class FoodDataCentralSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : FoodDataCentralSettingsRepository {
    override fun observe(): Flow<FoodDataCentralSettings> =
        dataStore.data.map { prefs ->
            FoodDataCentralSettings(
                remoteEnabled = prefs[remoteEnabled] ?: false,
                apiKey = prefs[usdaApiKey]?.let { SoftwareEncrypted(it) },
            )
        }

    override suspend fun save(settings: FoodDataCentralSettings) {
        dataStore.edit { prefs ->
            prefs[remoteEnabled] = settings.remoteEnabled
            prefs[usdaApiKey] = settings.apiKey?.data
        }
    }

    override suspend fun update(transform: (FoodDataCentralSettings) -> FoodDataCentralSettings) {
        save(transform(observe().first()))
    }

    private companion object {
        private val remoteEnabled = booleanPreferencesKey("fooddatacentral:remoteEnabled")
        private val usdaApiKey = byteArrayPreferencesKey("fooddatacentral:usda_api_key")
    }
}
