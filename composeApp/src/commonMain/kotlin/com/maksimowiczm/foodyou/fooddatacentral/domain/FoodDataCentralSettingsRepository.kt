package com.maksimowiczm.foodyou.fooddatacentral.domain

import kotlinx.coroutines.flow.Flow

interface FoodDataCentralSettingsRepository {
    fun observe(): Flow<FoodDataCentralSettings>

    suspend fun save(settings: FoodDataCentralSettings)

    suspend fun update(transform: (FoodDataCentralSettings) -> FoodDataCentralSettings)
}
