package com.maksimowiczm.foodyou.food.search.domain

import kotlinx.coroutines.flow.Flow

interface FoodSearchPreferencesRepository {
    fun observe(): Flow<FoodSearchPreferences>

    suspend fun save(preferences: FoodSearchPreferences)
}
