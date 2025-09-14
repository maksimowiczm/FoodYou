package com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.app.business.opensource.domain.search.FoodSearchPreferences
import com.maksimowiczm.foodyou.app.infrastructure.opensource.shared.datastore.AbstractDataStoreUserPreferencesRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.shared.datastore.set

internal class DataStoreFoodSearchPreferencesRepository(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<FoodSearchPreferences>(dataStore) {
    override fun Preferences.toUserPreferences(): FoodSearchPreferences =
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

    override fun MutablePreferences.applyUserPreferences(updated: FoodSearchPreferences) {
        this[FoodPreferencesKeys.UseOpenFoodFacts] = updated.openFoodFacts.enabled
        this[FoodPreferencesKeys.UseUsda] = updated.usda.enabled
        this[FoodPreferencesKeys.UsdaApiKey] = updated.usda.apiKey
    }
}

private object FoodPreferencesKeys {
    val UseOpenFoodFacts = booleanPreferencesKey("food:use_open_food_facts")
    val UseUsda = booleanPreferencesKey("food:use_usda")
    val UsdaApiKey = stringPreferencesKey("food:usda_api_key")
}
