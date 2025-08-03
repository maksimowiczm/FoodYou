package com.maksimowiczm.foodyou.feature.usda

sealed class USDAException(message: String) : Exception(message) {

    class ProductNotFoundException :
        USDAException("Product not found in the USDA database. Please check the product ID.")

    class ApiKeyIsMissingException :
        USDAException("USDA API key is missing. Please check your configuration.")

    class RateLimitException :
        USDAException("USDA API rate limit exceeded. Please try again later.")

    class ApiKeyInvalidException :
        USDAException("Invalid USDA API key. Please check your configuration.")

    class ApiKeyDisabledException :
        USDAException("USDA API key is disabled. Please check your configuration.")

    class ApiKeyUnauthorizedException :
        USDAException("USDA API key is unauthorized. Please check your configuration.")

    class ApiKeyUnverifiedException :
        USDAException("USDA API key is not verified. Please check your configuration.")
}
