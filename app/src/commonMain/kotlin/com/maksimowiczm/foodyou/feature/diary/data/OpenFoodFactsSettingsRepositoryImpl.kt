package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.feature.diary.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.system.data.model.Country
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class OpenFoodFactsSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val systemInfoRepository: SystemInfoRepository,
    private val openFoodFactsDao: OpenFoodFactsDao
) : OpenFoodFactsSettingsRepository {

    override fun observeOpenFoodFactsEnabled() = dataStore
        .observe(OpenFoodFactsPreferences.isEnabled)
        .map { it ?: false }

    override fun observeOpenFoodFactsCountry() = dataStore
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
    override fun observeOpenFoodFactsShowSearchHint() = combine(
        observeOpenFoodFactsEnabled(),
        dataStore.observe(OpenFoodFactsPreferences.hideSearchHint).map { it ?: false }
    ) { isEnabled, hideSearchHint ->
        !isEnabled && !hideSearchHint
    }

    override suspend fun hideOpenFoodFactsSearchHint() {
        dataStore.set(OpenFoodFactsPreferences.hideSearchHint to true)
    }

    override suspend fun enableOpenFoodFacts() {
        val country = dataStore.get(OpenFoodFactsPreferences.countryCode)
            ?: systemInfoRepository.defaultCountry.code

        dataStore.set(
            OpenFoodFactsPreferences.isEnabled to true,
            OpenFoodFactsPreferences.countryCode to country
        )
    }

    override suspend fun disableOpenFoodFacts() {
        dataStore.set(
            OpenFoodFactsPreferences.isEnabled to false,
            OpenFoodFactsPreferences.hideSearchHint to false
        )
    }

    override suspend fun setOpenFoodFactsCountry(country: Country) {
        dataStore.set(OpenFoodFactsPreferences.countryCode to country.code)
    }

    override suspend fun clearCache() {
        openFoodFactsDao.clearPagingKeys()
    }
}
