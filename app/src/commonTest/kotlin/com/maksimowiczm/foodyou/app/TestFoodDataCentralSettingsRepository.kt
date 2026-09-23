package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettings
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestFoodDataCentralSettingsRepository(private val settings: FoodDataCentralSettings) :
    FoodDataCentralSettingsRepository {
    override fun observe(): Flow<FoodDataCentralSettings> = flowOf(settings)

    override suspend fun save(settings: FoodDataCentralSettings) {
        error("Provide settings using constructor")
    }

    override suspend fun update(transform: (FoodDataCentralSettings) -> FoodDataCentralSettings) {
        error("Provide settings using constructor")
    }
}
