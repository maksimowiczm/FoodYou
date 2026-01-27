package com.maksimowiczm.foodyou.food.domain

sealed class FoodDatabaseError(message: String?) : Exception(message) {
    class Unknown(message: String?) : FoodDatabaseError(message)

    data object ProductNotFound : FoodDatabaseError("Product not found.")

    /** Errors related to FoodData Central API */
    sealed class FoodDataCentral(message: String) : FoodDatabaseError(message) {
        data object ApiKeyIsMissing :
            FoodDataCentral("USDA API key is missing. Please check your configuration.")

        data object RateLimitExceeded :
            FoodDataCentral("USDA API rate limit exceeded. Please try again later.")

        data object ApiKeyInvalid :
            FoodDataCentral("Invalid USDA API key. Please check your configuration.")

        data object ApiKeyDisabled :
            FoodDataCentral("USDA API key is disabled. Please check your configuration.")

        data object ApiKeyUnauthorized :
            FoodDataCentral("USDA API key is unauthorized. Please check your configuration.")

        data object ApiKeyUnverified :
            FoodDataCentral("USDA API key is not verified. Please check your configuration.")
    }
}
