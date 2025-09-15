package com.maksimowiczm.foodyou.app.business.opensource.domain.config

import com.maksimowiczm.foodyou.app.business.shared.domain.config.AppConfig

interface OpenSourceAppConfig : AppConfig {
    val sourceCodeUrl: String
    val issueTrackerUrl: String
    val featureRequestUrl: String
}
