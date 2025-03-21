package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.system.data.model.Country
import kotlinx.coroutines.flow.Flow

interface OpenFoodFactsSettingsRepository {
    fun observeOpenFoodFactsEnabled(): Flow<Boolean>
    fun observeOpenFoodFactsCountry(): Flow<Country?>

    /**
     * Whether the search hint should be shown.
     */
    fun observeOpenFoodFactsShowSearchHint(): Flow<Boolean>
    suspend fun hideOpenFoodFactsSearchHint()

    suspend fun enableOpenFoodFacts()
    suspend fun disableOpenFoodFacts()
    suspend fun setOpenFoodFactsCountry(country: Country)

    suspend fun clearCache()
}
