package com.maksimowiczm.foodyou.app.infrastructure.config

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.app.domain.AppConfig

class FoodYouConfig : AppConfig {
    override val versionName: String = BuildConfig.VERSION_NAME
    override val termsOfUseUri: String = BuildConfig.TERMS_OF_USE_URI
    override val privacyPolicyUri: String = BuildConfig.PRIVACY_POLICY_URI
    override val openFoodFactsTermsOfUseUri: String = BuildConfig.OPEN_FOOD_FACTS_TERMS_OF_USE_URI
    override val openFoodFactsPrivacyPolicyUri: String =
        BuildConfig.OPEN_FOOD_FACTS_PRIVACY_POLICY_URI
    override val foodDataCentralPrivacyPolicyUri: String =
        BuildConfig.FOOD_DATA_CENTRAL_PRIVACY_POLICY_URI
}
