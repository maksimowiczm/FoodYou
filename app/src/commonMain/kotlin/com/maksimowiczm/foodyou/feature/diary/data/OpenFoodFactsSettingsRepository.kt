package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.feature.diary.data.preferences.OpenFoodFactsPreferences
import com.maksimowiczm.foodyou.feature.diary.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.system.data.model.Country
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import com.maksimowiczm.foodyou.infrastructure.datastore.setNull
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class OpenFoodFactsSettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val systemInfoRepository: SystemInfoRepository,
    private val openFoodFactsDao: OpenFoodFactsDao
) {
    fun observeOpenFoodFactsEnabled() = dataStore
        .observe(OpenFoodFactsPreferences.isEnabled)
        .map { it ?: false }

    fun observeOpenFoodFactsCountry() = dataStore
        .observe(OpenFoodFactsPreferences.countryCode)
        .map {
            systemInfoRepository.countries.find { country ->
                it?.compareTo(
                    other = country.code,
                    ignoreCase = true
                ) == 0
            }
        }

    // Show search hint if Open Food Facts is disabled and the hint is not hidden
    fun observeOpenFoodFactsShowSearchHint() = combine(
        observeOpenFoodFactsEnabled(),
        dataStore.observe(OpenFoodFactsPreferences.hideSearchHint).map { it ?: false }
    ) { isEnabled, hideSearchHint ->
        !isEnabled && !hideSearchHint
    }

    suspend fun hideOpenFoodFactsSearchHint() {
        dataStore.set(OpenFoodFactsPreferences.hideSearchHint to true)
    }

    suspend fun enableOpenFoodFacts() {
        dataStore.set(
            OpenFoodFactsPreferences.isEnabled to true
        )
    }

    suspend fun disableOpenFoodFacts() {
        dataStore.set(
            OpenFoodFactsPreferences.isEnabled to false,
            OpenFoodFactsPreferences.hideSearchHint to false
        )
    }

    suspend fun setOpenFoodFactsCountry(country: Country?) {
        if (country == null) {
            dataStore.setNull(OpenFoodFactsPreferences.countryCode)
        } else {
            dataStore.set(OpenFoodFactsPreferences.countryCode to country.code)
        }
    }

    suspend fun clearCache() {
        openFoodFactsDao.clearPagingKeys()
    }
}
