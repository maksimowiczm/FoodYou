package com.maksimowiczm.foodyou.foodsearch.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FoodSearchPreferencesRepositoryImpl(private val dataStore: DataStore<Preferences>) :
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
    val allowOpenFoodFacts = booleanPreferencesKey("foodsearch:allowOpenFoodFacts")
    val allowFoodDataCentralUSDA = booleanPreferencesKey("foodsearch:allowFoodDataCentralUSDA")
}
