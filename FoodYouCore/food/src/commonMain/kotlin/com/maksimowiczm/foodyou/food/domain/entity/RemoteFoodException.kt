package com.maksimowiczm.foodyou.food.domain.entity

sealed class RemoteFoodException(message: String?) : Exception(message) {
    class ProductNotFoundException : RemoteFoodException("Product not found.")

    class Unknown(message: String?) : RemoteFoodException(message)

    sealed class USDA(message: String) : RemoteFoodException(message) {

        class ApiKeyIsMissingException :
            USDA("USDA API key is missing. Please check your configuration.")

        class RateLimitException : USDA("USDA API rate limit exceeded. Please try again later.")

        class ApiKeyInvalidException :
            USDA("Invalid USDA API key. Please check your configuration.")

        class ApiKeyDisabledException :
            USDA("USDA API key is disabled. Please check your configuration.")

        class ApiKeyUnauthorizedException :
            USDA("USDA API key is unauthorized. Please check your configuration.")

        class ApiKeyUnverifiedException :
            USDA("USDA API key is not verified. Please check your configuration.")
    }

    sealed class OpenFoodFacts(message: String) : RemoteFoodException(message) {
        class RateLimit :
            OpenFoodFacts("OpenFoodFacts API rate limit exceeded. Please try again later.")
    }

    companion object {
        fun fromThrowable(throwable: Throwable): RemoteFoodException =
            when (throwable) {
                is RemoteFoodException -> throwable
                else -> Unknown(throwable.message)
            }
    }
}
