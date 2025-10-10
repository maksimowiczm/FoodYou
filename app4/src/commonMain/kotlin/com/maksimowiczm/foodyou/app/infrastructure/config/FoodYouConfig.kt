package com.maksimowiczm.foodyou.app.infrastructure.config

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.app.domain.AppConfig

class FoodYouConfig : AppConfig {
    override val versionName: String = BuildConfig.VERSION_NAME
    override val termsOfUseUri: String = BuildConfig.TERMS_OF_USE_URI
    override val privacyPolicyUri: String = BuildConfig.PRIVACY_POLICY_URI
}
