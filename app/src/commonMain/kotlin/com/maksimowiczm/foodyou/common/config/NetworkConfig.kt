package com.maksimowiczm.foodyou.common.config

interface NetworkConfig {
    val userAgent: String
    val openFoodFactsApiUrl: String
    val usdaApiUrl: String

    val sponsorshipApiUrl: String
}
