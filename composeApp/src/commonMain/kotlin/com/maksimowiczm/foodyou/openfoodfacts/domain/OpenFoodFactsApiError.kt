package com.maksimowiczm.foodyou.openfoodfacts.domain

sealed class OpenFoodFactsApiError : Exception {

    constructor(inner: Exception) : super(inner)

    constructor(message: String) : super(message)

    class ProductNotFound : OpenFoodFactsApiError("Product not found")

    class RateLimitExceeded :
        OpenFoodFactsApiError("OpenFoodFacts API rate limit exceeded. Please try again later.")

    class Unknown(inner: Exception) : OpenFoodFactsApiError(inner)
}
