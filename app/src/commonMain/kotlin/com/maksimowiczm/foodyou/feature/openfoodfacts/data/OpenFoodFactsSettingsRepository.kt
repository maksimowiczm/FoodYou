package com.maksimowiczm.foodyou.feature.openfoodfacts.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.core.ext.get
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.core.ext.setNull
import com.maksimowiczm.foodyou.core.util.Country
import com.maksimowiczm.foodyou.core.util.SystemDetails
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsSettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val systemDetails: SystemDetails,
    database: FoodYouDatabase
) {
    private val openFoodFactsDao: OpenFoodFactsDao = database.openFoodFactsDao

    fun observeOpenFoodFactsEnabled() = dataStore
        .observe(OpenFoodFactsPreferences.isEnabled)
        .map { it ?: false }

    fun observeOpenFoodFactsCountry() = dataStore
        .observe(OpenFoodFactsPreferences.countryCode)
        .map {
            systemDetails.countries.find { country ->
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
        val country = dataStore.get(OpenFoodFactsPreferences.countryCode)
            ?: systemDetails.defaultCountry.code

        dataStore.set(
            OpenFoodFactsPreferences.isEnabled to true,
            OpenFoodFactsPreferences.countryCode to country
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
