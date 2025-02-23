package com.maksimowiczm.foodyou.feature.openfoodfacts.data

import com.maksimowiczm.foodyou.feature.system.data.model.Country
import kotlinx.coroutines.flow.Flow

interface OpenFoodFactsSettingsRepository {
    fun observeOpenFoodFactsEnabled(): Flow<Boolean>
    fun observeOpenFoodFactsCountry(): Flow<Country?>

    suspend fun enableOpenFoodFacts()
    suspend fun disableOpenFoodFacts()
    suspend fun setOpenFoodFactsCountry(country: Country)

    suspend fun clearCache()
}
