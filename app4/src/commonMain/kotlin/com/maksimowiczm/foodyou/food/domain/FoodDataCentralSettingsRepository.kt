package com.maksimowiczm.foodyou.food.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface FoodDataCentralSettingsRepository {
    fun observe(): Flow<FoodDataCentralSettings>

    suspend fun load(): FoodDataCentralSettings = observe().first()

    suspend fun save(settings: FoodDataCentralSettings)
}
