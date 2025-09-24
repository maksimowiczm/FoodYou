package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

internal class OpenFoodFactsFacade(
    val remoteDataSource: OpenFoodFactsRemoteDataSource,
    val openFoodFactsProductMapper: OpenFoodFactsProductMapper,
) {

    /** Extracts the barcode from a given Open Food Facts product URL. */
    fun extractBarcode(url: String): String? {
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }

    /**
     * Creates a request to fetch product details from Open Food Facts using the provided barcode.
     */
    fun createRequest(barcode: String) =
        OpenFoodFactsProductRequest(
            dataSource = remoteDataSource,
            barcode = barcode,
            mapper = openFoodFactsProductMapper,
        )

    /** Checks if the given URL matches the Open Food Facts product URL pattern. */
    fun matches(url: String): Boolean = regex.containsMatchIn(url)
}

private val regex by lazy {
    Regex(
        pattern = "(?:https://)?(?:www\\.)?(?:.+\\.)?openfoodfacts\\.org/.+/(\\d+)/*",
        options = setOf(RegexOption.IGNORE_CASE),
    )
}
