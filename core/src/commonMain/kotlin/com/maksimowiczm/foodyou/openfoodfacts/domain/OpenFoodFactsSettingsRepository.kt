package com.maksimowiczm.foodyou.openfoodfacts.domain

import kotlinx.coroutines.flow.Flow

interface OpenFoodFactsSettingsRepository {
    fun observe(): Flow<OpenFoodFactsSettings>

    suspend fun save(settings: OpenFoodFactsSettings)

    suspend fun update(transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings)
}
