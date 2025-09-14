package com.maksimowiczm.foodyou.app.infrastructure.opensource.network

import com.maksimowiczm.foodyou.app.business.opensource.domain.config.AppConfig
import com.maksimowiczm.foodyou.app.business.opensource.domain.config.NetworkConfig
import com.maksimowiczm.foodyou.app.infrastructure.opensource.BuildConfig

internal class FoodYouNetworkConfig(appConfig: AppConfig) : NetworkConfig {
    override val userAgent: String =
        "Food You/${appConfig.versionName} (${appConfig.sourceCodeUrl})"
    override val sponsorshipApiUrl: String = BuildConfig.SPONSOR_API_URL
    override val openFoodFactsApiUrl: String = BuildConfig.OPEN_FOOD_FACTS_URL
    override val usdaApiUrl: String = BuildConfig.USDA_URL
}
