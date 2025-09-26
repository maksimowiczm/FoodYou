package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.common.config.AppConfig
import com.maksimowiczm.foodyou.common.config.NetworkConfig

internal class FoodYouNetworkConfig(appConfig: AppConfig) : NetworkConfig {
    override val userAgent: String =
        "Food You/${appConfig.versionName} (${appConfig.sourceCodeUrl})"
    override val openFoodFactsApiUrl: String = BuildConfig.OPEN_FOOD_FACTS_URL
    override val usdaApiUrl: String = BuildConfig.USDA_URL
    override val githubSponsorsRepositoryUrl: String = BuildConfig.GITHUB_SPONSORS_REPOSITORY_URL
}
