package com.maksimowiczm.foodyou.food.search.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodSearchPreferencesRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    FoodSearchPreferencesRepository {
    override fun observe(): Flow<FoodSearchPreferences> {
        return dataStore.data.map { preferences ->
            FoodSearchPreferences(
                allowOpenFoodFacts =
                    preferences[FoodSearchPreferencesKeys.allowOpenFoodFacts] ?: false,
                allowFoodDataCentralUSDA =
                    preferences[FoodSearchPreferencesKeys.allowFoodDataCentralUSDA] ?: false,
            )
        }
    }

    override suspend fun save(preferences: FoodSearchPreferences) {
        dataStore.edit {
            it[FoodSearchPreferencesKeys.allowOpenFoodFacts] = preferences.allowOpenFoodFacts
            it[FoodSearchPreferencesKeys.allowFoodDataCentralUSDA] =
                preferences.allowFoodDataCentralUSDA
        }
    }
}

private object FoodSearchPreferencesKeys {
    val allowOpenFoodFacts = booleanPreferencesKey("food:search:allow_open_food_facts")
    val allowFoodDataCentralUSDA = booleanPreferencesKey("food:search:allow_food_data_central_usda")
}
