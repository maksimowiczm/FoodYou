package com.maksimowiczm.foodyou.core.feature.product.data

import com.maksimowiczm.foodyou.core.feature.system.data.model.Country
import kotlinx.coroutines.flow.Flow

interface DatabaseSettingsRepository {
    fun observeOpenFoodFactsEnabled(): Flow<Boolean>
    fun observeOpenFoodFactsCountry(): Flow<Country?>

    suspend fun enableOpenFoodFacts()
    suspend fun disableOpenFoodFacts()
    suspend fun setOpenFoodFactsCountry(country: Country)
}
