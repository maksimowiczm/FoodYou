package com.maksimowiczm.foodyou.food.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.food.domain.FoodDataCentralSettings
import com.maksimowiczm.foodyou.food.domain.FoodDataCentralSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodDataCentralSettingsRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    FoodDataCentralSettingsRepository {
    override fun observe(): Flow<FoodDataCentralSettings> =
        dataStore.data.map {
            FoodDataCentralSettings(apiKey = it[PreferencesKeys.usdaApiKey]?.ifBlank { null })
        }

    override suspend fun save(settings: FoodDataCentralSettings) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[PreferencesKeys.usdaApiKey] = settings.apiKey ?: ""
            }
        }
    }
}

private object PreferencesKeys {
    val usdaApiKey = stringPreferencesKey("food:usda_api_key")
}
