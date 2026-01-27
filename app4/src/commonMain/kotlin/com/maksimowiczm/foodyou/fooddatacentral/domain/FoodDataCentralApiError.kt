package com.maksimowiczm.foodyou.fooddatacentral.domain

sealed class FoodDataCentralApiError(message: String) : Exception(message) {
    class ProductNotFound : FoodDataCentralApiError("Product not found")

    class ApiKeyIsMissing :
        FoodDataCentralApiError("USDA API key is missing. Please check your configuration.")

    class RateLimitExceeded :
        FoodDataCentralApiError("USDA API rate limit exceeded. Please try again later.")

    class ApiKeyInvalid :
        FoodDataCentralApiError("Invalid USDA API key. Please check your configuration.")

    class ApiKeyDisabled :
        FoodDataCentralApiError("USDA API key is disabled. Please check your configuration.")

    class ApiKeyUnauthorized :
        FoodDataCentralApiError("USDA API key is unauthorized. Please check your configuration.")

    class ApiKeyUnverified :
        FoodDataCentralApiError("USDA API key is not verified. Please check your configuration.")
}
