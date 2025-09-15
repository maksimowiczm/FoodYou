package com.maksimowiczm.foodyou.app.infrastructure.opensource.network

import com.maksimowiczm.foodyou.app.business.opensource.domain.config.OpenSourceAppConfig
import com.maksimowiczm.foodyou.app.business.opensource.domain.config.OpenSourceNetworkConfig
import com.maksimowiczm.foodyou.app.infrastructure.opensource.BuildConfig

internal class FoodYouNetworkConfig(appConfig: OpenSourceAppConfig) : OpenSourceNetworkConfig {
    override val userAgent: String =
        "Food You/${appConfig.versionName} (${appConfig.sourceCodeUrl})"
    override val sponsorshipApiUrl: String = BuildConfig.SPONSOR_API_URL
    override val openFoodFactsApiUrl: String = BuildConfig.OPEN_FOOD_FACTS_URL
    override val usdaApiUrl: String = BuildConfig.USDA_URL
}
