package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig

/**
 * Central configuration for the FoodYou application.
 *
 * @param versionName The current app version name (e.g. "1.2.3")
 */
class FoodYouConfig(val versionName: String) : NetworkConfig {
    val privacyPolicyUri = "https://foodyou.maksimowiczm.com/privacy-policy"
    val openFoodFactsTermsOfUseUri = "https://world.openfoodfacts.org/terms-of-use"
    val openFoodFactsPrivacyPolicyUri = "https://world.openfoodfacts.org/privacy"
    val openFoodFactsRegisterUri = "https://world.openfoodfacts.org/cgi/user.pl"
    val foodDataCentralPrivacyPolicyUri = "https://www.usda.gov/privacy-policy"
    val foodDataCentralObtainApiKeyUri = "https://fdc.nal.usda.gov/api-key-signup"
    val sourceCodeUri = "https://github.com/maksimowiczm/FoodYou"
    val featureRequestUri = "https://github.com/maksimowiczm/FoodYou/issues"
    val bugReportUri = "https://github.com/maksimowiczm/FoodYou/issues"
    val emailContactUri =
        "mailto:maksimowicz.dev@gmail.com?subject=Food You Feedback&body=Food You Version: $versionName\n"
    val translateUri = "https://crowdin.com/project/food-you"
    val changelogUri = "https://github.com/maksimowiczm/FoodYou/releases/tag/$versionName"

    override val userAgent = "Food You/$versionName ($sourceCodeUri)"
}
