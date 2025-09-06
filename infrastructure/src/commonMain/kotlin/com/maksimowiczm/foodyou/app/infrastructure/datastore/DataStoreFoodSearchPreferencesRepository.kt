package com.maksimowiczm.foodyou.app.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreFoodSearchPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository<FoodSearchPreferences> {

    override fun observe(): Flow<FoodSearchPreferences> =
        dataStore.data.map(Preferences::toFoodSearchPreferences)

    override suspend fun update(transform: FoodSearchPreferences.() -> FoodSearchPreferences) {
        dataStore.updateData { currentPreferences ->
            val currentFoodPreferences = currentPreferences.toFoodSearchPreferences()
            val newFoodPreferences = transform(currentFoodPreferences)
            currentPreferences.toMutablePreferences().apply {
                updateFoodSearchPreferences(newFoodPreferences)
            }
        }
    }
}

private fun Preferences.toFoodSearchPreferences() =
    FoodSearchPreferences(
        openFoodFacts =
            FoodSearchPreferences.OpenFoodFacts(
                enabled = this[FoodPreferencesKeys.UseOpenFoodFacts] ?: false
            ),
        usda =
            FoodSearchPreferences.Usda(
                enabled = this[FoodPreferencesKeys.UseUsda] ?: false,
                apiKey = this[FoodPreferencesKeys.UsdaApiKey],
            ),
    )

private fun MutablePreferences.updateFoodSearchPreferences(
    foodSearchPreferences: FoodSearchPreferences
) {
    this[FoodPreferencesKeys.UseOpenFoodFacts] = foodSearchPreferences.openFoodFacts.enabled
    this[FoodPreferencesKeys.UseUsda] = foodSearchPreferences.usda.enabled

    when (val apiKey = foodSearchPreferences.usda.apiKey) {
        null -> remove(FoodPreferencesKeys.UsdaApiKey)
        else -> this[FoodPreferencesKeys.UsdaApiKey] = apiKey
    }
}

private object FoodPreferencesKeys {
    val UseOpenFoodFacts = booleanPreferencesKey("food:use_open_food_facts")
    val UseUsda = booleanPreferencesKey("food:use_usda")
    val UsdaApiKey = stringPreferencesKey("food:usda_api_key")
}
