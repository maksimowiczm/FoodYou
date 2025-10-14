package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.flow.Flow

interface FoodSearchHistoryRepository {
    fun observe(profileId: ProfileId): Flow<FoodSearchHistory>

    suspend fun save(foodSearchHistory: FoodSearchHistory)
}
