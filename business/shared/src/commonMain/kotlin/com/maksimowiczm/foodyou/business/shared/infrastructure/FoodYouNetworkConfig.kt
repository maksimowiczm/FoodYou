package com.maksimowiczm.foodyou.business.shared.infrastructure

import FoodYou.business.shared.BuildConfig
import com.maksimowiczm.foodyou.business.shared.application.config.AppConfig
import com.maksimowiczm.foodyou.business.shared.application.network.NetworkConfig

internal class FoodYouNetworkConfig(appConfig: AppConfig) : NetworkConfig {
    override val userAgent: String = "Food You/${appConfig.versionName}"
    override val sponsorshipApiUrl: String = BuildConfig.SPONSOR_API_URL
}
