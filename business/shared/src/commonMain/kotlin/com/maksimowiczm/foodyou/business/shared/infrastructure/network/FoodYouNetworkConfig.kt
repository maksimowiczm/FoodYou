package com.maksimowiczm.foodyou.business.shared.infrastructure.network

import FoodYou.business.shared.BuildConfig
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.config.AppConfig
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.network.NetworkConfig

internal class FoodYouNetworkConfig(appConfig: AppConfig) : NetworkConfig {
    override val userAgent: String = "Food You/${appConfig.versionName}"
    override val sponsorshipApiUrl: String = BuildConfig.SPONSOR_API_URL
}
