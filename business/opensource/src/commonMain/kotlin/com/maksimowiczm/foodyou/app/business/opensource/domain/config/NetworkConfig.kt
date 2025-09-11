package com.maksimowiczm.foodyou.app.business.opensource.domain.config

interface NetworkConfig {
    val userAgent: String
    val sponsorshipApiUrl: String
    val openFoodFactsApiUrl: String
    val usdaApiUrl: String
}
