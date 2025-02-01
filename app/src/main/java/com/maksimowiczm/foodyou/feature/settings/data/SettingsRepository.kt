package com.maksimowiczm.foodyou.feature.settings.data

import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.feature.system.data.model.Country
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeOpenFoodFactsEnabled(): Flow<Boolean>
    fun observeOpenFoodFactsCountry(): Flow<Country?>
    suspend fun enableOpenFoodFacts()
    suspend fun disableOpenFoodFacts()
    suspend fun setOpenFoodFactsCountry(country: Country)

    fun observeDailyGoals(): Flow<DailyGoals>
    suspend fun setDailyGoals(goals: DailyGoals)

    /**
     * The default country code of the system's locale.
     *
     * This value is derived from the system configuration and represents the ISO 3166-1 alpha-2 country code.
     */
    val defaultCountry: Country

    /**
     * The list of countries available on the system.
     */
    val countries: List<Country>
}
