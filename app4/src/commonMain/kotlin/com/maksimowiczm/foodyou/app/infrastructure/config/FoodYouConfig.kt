package com.maksimowiczm.foodyou.app.infrastructure.config

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.common.NetworkConfig

class FoodYouConfig : AppConfig, NetworkConfig {
    override val versionName: String = "4.0.0-dev"
    override val termsOfUseUri: String = BuildConfig.TERMS_OF_USE_URI
    override val privacyPolicyUri: String = BuildConfig.PRIVACY_POLICY_URI
    override val openFoodFactsTermsOfUseUri: String = BuildConfig.OPEN_FOOD_FACTS_TERMS_OF_USE_URI
    override val openFoodFactsPrivacyPolicyUri: String =
        BuildConfig.OPEN_FOOD_FACTS_PRIVACY_POLICY_URI
    override val foodDataCentralPrivacyPolicyUri: String =
        BuildConfig.FOOD_DATA_CENTRAL_PRIVACY_POLICY_URI
    override val sourceCodeUri: String = BuildConfig.SOURCE_CODE_URI
    override val featureRequestUri: String = BuildConfig.FEATURE_REQUEST_URI
    override val emailContactUri: String = BuildConfig.FEEDBACK_EMAIL_URI
    override val translateUri: String = BuildConfig.CROWDIN_URI

    override val userAgent: String = "Food You/$versionName ($sourceCodeUri)"
}
