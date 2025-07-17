package com.maksimowiczm.foodyou.feature.food.data.network.usda

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.preferences.UsdaApiKey
import com.maksimowiczm.foodyou.feature.usda.USDARemoteDataSource

internal class USDAFacade(
    private val dataSource: USDARemoteDataSource,
    dataStore: DataStore<Preferences>,
    private val mapper: USDAProductMapper
) {
    private val apiKey = dataStore.userPreference<UsdaApiKey>()

    /**
     * Extracts the ID from a given USDA product URL.
     */
    fun extractId(url: String): String? = try {
        regex.find(url)?.groups?.get(1)?.value
    } catch (e: Exception) {
        Logger.w(TAG, e) { "Failed to extract ID from URL: $url" }
        null
    }

    /**
     * Creates a request to fetch product details from USDA using the provided ID.
     */
    suspend fun createRequest(id: String) = USDAProductRequest(
        dataSource = dataSource,
        apiKey = apiKey.getBlocking() ?: "DEMO_KEY",
        id = id,
        mapper = mapper
    )

    /**
     * Checks if the given URL matches the USDA product URL pattern.
     */
    fun matches(url: String): Boolean = regex.containsMatchIn(url)

    private companion object {
        private const val TAG = "USDAFacade"
    }
}

private val regex by lazy {
    Regex(
        pattern = "(?:https://)?(?:www\\.)?(?:.+\\.)?fdc\\.nal\\.usda\\.gov/food-details/(\\d+)",
        options = setOf(RegexOption.IGNORE_CASE)
    )
}
