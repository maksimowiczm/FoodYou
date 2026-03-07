package com.maksimowiczm.foodyou.foodsearch.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface FoodSearchPreferencesRepository {
    fun observe(): Flow<FoodSearchPreferences>

    suspend fun save(preferences: FoodSearchPreferences)
}

suspend fun FoodSearchPreferencesRepository.update(
    transform: (FoodSearchPreferences) -> FoodSearchPreferences
) {
    val currentPreferences = observe().first()
    val newPreferences = transform(currentPreferences)
    save(newPreferences)
}
