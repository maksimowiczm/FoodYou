package com.maksimowiczm.foodyou.app.business.opensource.domain.config

import com.maksimowiczm.foodyou.app.business.shared.domain.config.NetworkConfig

interface OpenSourceNetworkConfig : NetworkConfig {
    val sponsorshipApiUrl: String
}
