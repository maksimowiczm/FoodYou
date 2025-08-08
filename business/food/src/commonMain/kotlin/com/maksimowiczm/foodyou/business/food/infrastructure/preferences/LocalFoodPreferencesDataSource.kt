package com.maksimowiczm.foodyou.business.food.infrastructure.preferences

import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import kotlinx.coroutines.flow.Flow

internal interface LocalFoodPreferencesDataSource {
    fun observe(): Flow<FoodPreferences>

    suspend fun update(preferences: FoodPreferences)
}
