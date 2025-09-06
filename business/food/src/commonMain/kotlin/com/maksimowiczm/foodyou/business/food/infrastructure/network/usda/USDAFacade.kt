package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.shared.log.Logger
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository

internal class USDAFacade(
    private val dataSource: USDARemoteDataSource,
    private val mapper: USDAProductMapper,
    private val preferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
    private val logger: Logger,
) {
    /** Extracts the ID from a given USDA product URL. */
    fun extractId(url: String): String? =
        try {
            regex.find(url)?.groups?.get(1)?.value
        } catch (e: Exception) {
            logger.w(TAG, e) { "Failed to extract ID from URL: $url" }
            null
        }

    /** Creates a request to fetch product details from USDA using the provided ID. */
    fun createRequest(id: String) =
        USDAProductRequest(dataSource, id, mapper, preferencesRepository)

    /** Checks if the given URL matches the USDA product URL pattern. */
    fun matches(url: String): Boolean = regex.containsMatchIn(url)

    private companion object {
        private const val TAG = "USDAFacade"
    }
}

private val regex by lazy {
    Regex(
        pattern = "(?:https://)?(?:www\\.)?(?:.+\\.)?fdc\\.nal\\.usda\\.gov/food-details/(\\d+)",
        options = setOf(RegexOption.IGNORE_CASE),
    )
}
