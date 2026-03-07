package com.maksimowiczm.foodyou.openfoodfacts.domain

sealed class OpenFoodFactsApiError(message: String) : Exception(message) {
    class ProductNotFound : OpenFoodFactsApiError("Product not found")

    class RateLimitExceeded :
        OpenFoodFactsApiError("OpenFoodFacts API rate limit exceeded. Please try again later.")
}
