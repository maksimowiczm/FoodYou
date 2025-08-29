package com.maksimowiczm.foodyou.business.shared.infrastructure

import FoodYou.business.shared.BuildConfig
import com.maksimowiczm.foodyou.business.shared.domain.config.AppConfig
import com.maksimowiczm.foodyou.business.shared.domain.config.NetworkConfig

internal class FoodYouNetworkConfig(appConfig: AppConfig) : NetworkConfig {
    override val userAgent: String =
        "Food You/${appConfig.versionName} (${appConfig.sourceCodeUrl})"
    override val sponsorshipApiUrl: String = BuildConfig.SPONSOR_API_URL
    override val openFoodFactsApiUrl: String = BuildConfig.OPEN_FOOD_FACTS_URL
}
