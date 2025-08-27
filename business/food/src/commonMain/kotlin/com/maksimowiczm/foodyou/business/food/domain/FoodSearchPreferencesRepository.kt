package com.maksimowiczm.foodyou.business.food.domain

import kotlinx.coroutines.flow.Flow

interface FoodSearchPreferencesRepository {
    fun observe(): Flow<FoodSearchPreferences>

    suspend fun update(transform: FoodSearchPreferences.() -> FoodSearchPreferences)
}
