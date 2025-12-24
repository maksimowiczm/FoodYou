package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.common.config.AppConfig
import com.maksimowiczm.foodyou.common.config.NetworkConfig

internal class FoodYouConfig : AppConfig, NetworkConfig {
    override val versionName: String = BuildConfig.VERSION_NAME
    override val contactEmailUri: String =
        "mailto:maksimowicz.dev@gmail.com?subject=Food You Feedback&body=Food You Version: $versionName\n"
    override val translationUri: String = "https://foodyou.maksimowiczm.com/translate"
    override val sourceCodeUri: String = "https://github.com/maksimowiczm/FoodYou"
    override val issueTrackerUri: String = "https://github.com/maksimowiczm/FoodYou/issues"
    override val termsOfServiceUri: String = "https://TODO"
    override val privacyPolicyUri: String = "https://TODO"
    override val openFoodFactsTermsOfUseUri: String = "https://world.openfoodfacts.org/terms-of-use"
    override val openFoodFactsPrivacyPolicyUri: String = "https://world.openfoodfacts.org/privacy"
    override val foodDataCentralPrivacyPolicyUri: String = "https://www.usda.gov/privacy-policy"

    override val userAgent: String = "Food You/$versionName (maksimowicz.dev@gmail.com)"
}
