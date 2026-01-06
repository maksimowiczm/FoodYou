package com.maksimowiczm.foodyou.app.infrastructure.config

import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.common.domain.NetworkConfig

class FoodYouConfig : AppConfig, NetworkConfig {
    override val versionName: String = "4.0.0-dev"
    override val privacyPolicyUri: String = "https://foodyou.maksimowiczm.com/privacy-policy"
    override val openFoodFactsTermsOfUseUri: String = "https://world.openfoodfacts.org/terms-of-use"
    override val openFoodFactsPrivacyPolicyUri: String = "https://world.openfoodfacts.org/privacy"
    override val foodDataCentralPrivacyPolicyUri: String = "https://www.usda.gov/privacy-policy"
    override val sourceCodeUri: String = "https://github.com/maksimowiczm/FoodYou"
    override val featureRequestUri: String = "https://github.com/maksimowiczm/FoodYou/issues"
    override val emailContactUri: String =
        "mailto:maksimowicz.dev@gmail.com?subject=Food You Feedback&body=Food You Version: $versionName\n"
    override val translateUri: String = "https://crowdin.com/project/food-you"
    override val changelogUri: String = "https://github.com/maksimowiczm/FoodYou/releases"

    override val userAgent: String = "Food You/$versionName ($sourceCodeUri)"
}
