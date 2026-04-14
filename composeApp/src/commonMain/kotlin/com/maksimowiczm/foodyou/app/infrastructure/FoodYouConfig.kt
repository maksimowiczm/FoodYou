package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.common.application.AppConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig

class FoodYouConfig(override val versionName: String) : AppConfig, NetworkConfig {
    val privacyPolicyUri = "https://foodyou.maksimowiczm.com/privacy-policy"
    val openFoodFactsTermsOfUseUri = "https://world.openfoodfacts.org/terms-of-use"
    val openFoodFactsPrivacyPolicyUri = "https://world.openfoodfacts.org/privacy"
    val foodDataCentralPrivacyPolicyUri = "https://www.usda.gov/privacy-policy"
    val sourceCodeUri = "https://github.com/maksimowiczm/FoodYou"
    val featureRequestUri = "https://github.com/maksimowiczm/FoodYou/issues"
    val bugReportUri = "https://github.com/maksimowiczm/FoodYou/issues"
    val emailContactUri =
        "mailto:maksimowicz.dev@gmail.com?subject=Food You Feedback&body=Food You Version: $versionName\n"
    val translateUri = "https://crowdin.com/project/food-you"
    val changelogUri = "https://github.com/maksimowiczm/FoodYou/releases"

    override val userAgent = "Food You/$versionName ($sourceCodeUri)"
}
