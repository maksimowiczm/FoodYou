package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestOpenFoodFactsSettingsRepository(private val settings: OpenFoodFactsSettings) :
    OpenFoodFactsSettingsRepository {
    override fun observe(): Flow<OpenFoodFactsSettings> = flowOf(settings)

    override suspend fun save(settings: OpenFoodFactsSettings) {
        error("Provide settings using constructor")
    }

    override suspend fun update(transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings) {
        error("Provide settings using constructor")
    }
}
