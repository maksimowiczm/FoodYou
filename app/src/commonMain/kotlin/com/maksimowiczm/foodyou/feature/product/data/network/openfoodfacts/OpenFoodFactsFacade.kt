package com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductMatcher

internal class OpenFoodFactsFacade(
    val remoteDataSource: OpenFoodFactsRemoteDataSource,
    private val matcher: RemoteProductMatcher = openFoodFactsUrlMatcher
) : RemoteProductMatcher by matcher {
    fun extractBarcode(url: String): String? {
        val regex = Regex(
            pattern = "https://(?:www\\.)?(?:.+\\.)?openfoodfacts\\.org/product/(\\d+)/*",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }
}

private val openFoodFactsUrlMatcher = RemoteProductMatcher {
    val regex = Regex(
        pattern = "https://(?:www\\.)?(?:.+\\.)?openfoodfacts\\.org/product/\\d+/*",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    regex.containsMatchIn(it)
}
