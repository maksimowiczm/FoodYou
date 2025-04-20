package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct

interface OpenFoodFactsRemoteDataSource {
    /**
     * Fetches a product from Open Food Facts using the provided barcode and country code.
     *
     * @param code The barcode of the product to fetch.
     * @param country The country code to filter the product search (optional).
     *
     * @return An [OpenFoodFactsProduct] object if the product is found, or null if not found.
     */
    suspend fun getProduct(code: String, country: String?): OpenFoodFactsProduct?
}
