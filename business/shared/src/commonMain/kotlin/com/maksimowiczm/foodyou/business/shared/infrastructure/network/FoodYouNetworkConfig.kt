package com.maksimowiczm.foodyou.business.shared.infrastructure.network

import FoodYou.business.shared.BuildConfig
import com.maksimowiczm.foodyou.business.shared.domain.network.NetworkConfig
import com.maksimowiczm.foodyou.shared.common.domain.config.AppConfig

internal class FoodYouNetworkConfig(appConfig: AppConfig) : NetworkConfig {
    override val userAgent: String = "Food You/${appConfig.versionName}"
    override val sponsorshipApiUrl: String = BuildConfig.SPONSOR_API_URL
}
