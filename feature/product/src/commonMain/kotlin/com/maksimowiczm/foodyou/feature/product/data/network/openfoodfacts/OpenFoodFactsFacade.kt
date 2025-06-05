package com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductMatcher

internal class OpenFoodFactsFacade(
    val remoteDataSource: OpenFoodFactsRemoteDataSource,
    private val matcher: RemoteProductMatcher = openFoodFactsUrlMatcher
) : RemoteProductMatcher by matcher {
    fun extractBarcode(url: String): String? {
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }

    fun createRequest(barcode: String) = OpenFoodFactsProductRequest(
        dataSource = remoteDataSource,
        barcode = barcode
    )
}

private val regex by lazy {
    Regex(
        pattern = "(?:https://)?(?:www\\.)?(?:.+\\.)?openfoodfacts\\.org/product/(\\d+)/*",
        options = setOf(RegexOption.IGNORE_CASE)
    )
}

private val openFoodFactsUrlMatcher = RemoteProductMatcher { regex.containsMatchIn(it) }
