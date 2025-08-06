package com.maksimowiczm.foodyou.business.food.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import com.maksimowiczm.foodyou.business.food.domain.OpenFoodFactsPreferences
import com.maksimowiczm.foodyou.business.food.domain.UsdaPreferences
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.FoodPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreFoodPreferencesDataSource(private val dataStore: DataStore<Preferences>) :
    FoodPreferencesDataSource {

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

    override suspend fun update(preferences: FoodPreferences) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                set(FoodPreferencesKeys.UseOpenFoodFacts, preferences.openFoodFacts.enabled)
                set(FoodPreferencesKeys.UseUsda, preferences.usda.enabled)

                if (preferences.usda.apiKey != null) {
                    set(FoodPreferencesKeys.UsdaApiKey, preferences.usda.apiKey)
                } else {
                    remove(FoodPreferencesKeys.UsdaApiKey)
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
