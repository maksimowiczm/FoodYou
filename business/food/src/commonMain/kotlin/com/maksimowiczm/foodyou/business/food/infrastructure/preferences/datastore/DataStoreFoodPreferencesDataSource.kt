package com.maksimowiczm.foodyou.business.food.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import com.maksimowiczm.foodyou.business.food.domain.OpenFoodFactsPreferences
import com.maksimowiczm.foodyou.business.food.domain.UsdaPreferences
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreFoodPreferencesDataSource(private val dataStore: DataStore<Preferences>) :
    LocalFoodPreferencesDataSource {

    override fun observe(): Flow<FoodPreferences> =
        dataStore.data.map { preferences ->
            FoodPreferences(
                openFoodFacts =
                    OpenFoodFactsPreferences(
                        enabled = preferences[FoodPreferencesKeys.UseOpenFoodFacts] ?: false
                    ),
                usda =
                    UsdaPreferences(
                        enabled = preferences[FoodPreferencesKeys.UseUsda] ?: false,
                        apiKey = preferences[FoodPreferencesKeys.UsdaApiKey],
                    ),
            )
        }

    override suspend fun updateOpenFoodFactsEnabled(enabled: Boolean) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                set(FoodPreferencesKeys.UseOpenFoodFacts, enabled)
            }
        }
    }

    override suspend fun updateUsdaEnabled(enabled: Boolean) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                set(FoodPreferencesKeys.UseUsda, enabled)
            }
        }
    }

    override suspend fun updateUsdaApiKey(apiKey: String?) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                if (apiKey == null) {
                    remove(FoodPreferencesKeys.UsdaApiKey)
                } else {
                    set(FoodPreferencesKeys.UsdaApiKey, apiKey)
                }
            }
        }
    }
}

private object FoodPreferencesKeys {
    val UseOpenFoodFacts = booleanPreferencesKey("food:use_open_food_facts")
    val UseUsda = booleanPreferencesKey("food:use_usda")
    val UsdaApiKey = stringPreferencesKey("food:usda_api_key")
}
