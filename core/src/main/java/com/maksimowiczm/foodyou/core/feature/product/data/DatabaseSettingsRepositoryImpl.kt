package com.maksimowiczm.foodyou.core.feature.product.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.core.feature.system.data.model.Country
import com.maksimowiczm.foodyou.core.infrastructure.datastore.get
import com.maksimowiczm.foodyou.core.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.core.infrastructure.datastore.set
import kotlinx.coroutines.flow.map

class DatabaseSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val systemInfoRepository: SystemInfoRepository
) : DatabaseSettingsRepository {
    override fun observeOpenFoodFactsEnabled() = dataStore.observe(
        ProductPreferences.openFoodFactsEnabled
    ).map { it ?: false }

    override fun observeOpenFoodFactsCountry() = dataStore.observe(
        ProductPreferences.openFoodCountryCode
    ).map {
        systemInfoRepository.countries.find { country ->
            it?.compareTo(
                other = country.code,
                ignoreCase = true
            ) == 0
        }
    }

    override suspend fun enableOpenFoodFacts() {
        val country = dataStore.get(ProductPreferences.openFoodCountryCode)
            ?: systemInfoRepository.defaultCountry.code

        dataStore.set(
            ProductPreferences.openFoodFactsEnabled to true,
            ProductPreferences.openFoodCountryCode to country
        )
    }

    override suspend fun disableOpenFoodFacts() {
        dataStore.set(ProductPreferences.openFoodFactsEnabled to false)
    }

    override suspend fun setOpenFoodFactsCountry(country: Country) {
        dataStore.set(ProductPreferences.openFoodCountryCode to country.code)
    }
}
