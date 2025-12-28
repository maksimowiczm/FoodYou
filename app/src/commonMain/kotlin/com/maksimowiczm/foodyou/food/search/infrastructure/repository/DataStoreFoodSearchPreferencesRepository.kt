package com.maksimowiczm.foodyou.food.search.infrastructure.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.infrastructure.datastore.AbstractDataStoreUserPreferencesRepository
import com.maksimowiczm.foodyou.common.infrastructure.datastore.set
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferences

internal class DataStoreFoodSearchPreferencesRepository(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<FoodSearchPreferences>(dataStore) {
    override fun Preferences.toUserPreferences(): FoodSearchPreferences =
        FoodSearchPreferences(
            openFoodFacts =
                FoodSearchPreferences.OpenFoodFacts(
                    enabled = this[FoodPreferencesKeys.UseOpenFoodFacts] ?: false
                ),
        )

    override fun MutablePreferences.applyUserPreferences(updated: FoodSearchPreferences) {
        this[FoodPreferencesKeys.UseOpenFoodFacts] = updated.openFoodFacts.enabled
    }
}

private object FoodPreferencesKeys {
    val UseOpenFoodFacts = booleanPreferencesKey("food:use_open_food_facts")
}
