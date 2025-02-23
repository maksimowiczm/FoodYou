package com.maksimowiczm.foodyou.feature.openfoodfacts.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.system.data.model.Country
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.flow.map

class OpenFoodFactsSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val systemInfoRepository: SystemInfoRepository,
    openFoodFactsDatabase: OpenFoodFactsDatabase
) : OpenFoodFactsSettingsRepository {
    private val openFoodFactsDao = openFoodFactsDatabase.openFoodFactsDao()

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

    override suspend fun enableOpenFoodFacts() {
        val country = dataStore.get(OpenFoodFactsPreferences.countryCode)
            ?: systemInfoRepository.defaultCountry.code

        dataStore.set(
            OpenFoodFactsPreferences.isEnabled to true,
            OpenFoodFactsPreferences.countryCode to country
        )
    }

    override suspend fun disableOpenFoodFacts() {
        dataStore.set(OpenFoodFactsPreferences.isEnabled to false)
    }

    override suspend fun setOpenFoodFactsCountry(country: Country) {
        dataStore.set(OpenFoodFactsPreferences.countryCode to country.code)
    }

    override suspend fun clearCache() {
        openFoodFactsDao.clearPagingKeys()
    }
}
