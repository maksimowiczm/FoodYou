package com.maksimowiczm.foodyou.openfoodfacts.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface OpenFoodFactsSettingsRepository {
    fun observe(): Flow<OpenFoodFactsSettings>

    suspend fun save(settings: OpenFoodFactsSettings)

    suspend fun update(transform: (OpenFoodFactsSettings) -> OpenFoodFactsSettings)
}

fun OpenFoodFactsSettingsRepository.hasCredentials(): Flow<Boolean> =
    observe().map { it.login != null && it.password != null }
