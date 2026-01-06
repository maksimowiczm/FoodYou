package com.maksimowiczm.foodyou.common.config

interface AppConfig {

    /** Current version name of the application (e.g. "1.0.0"). */
    val versionName: String

    /** Mailto URI for contacting the developer (including subject and body). */
    val contactEmailUri: String

    /** URL to the translation platform where users can contribute translations. */
    val translationUri: String

    val sourceCodeUri: String
    val issueTrackerUri: String

    val privacyPolicyUri: String

    /** URI to the Open Food Facts Terms of Use document. */
    val openFoodFactsTermsOfUseUri: String

    /** URI to the Open Food Facts Privacy Policy document. */
    val openFoodFactsPrivacyPolicyUri: String

    /** URI to the FoodData Central Privacy Policy document. */
    val foodDataCentralPrivacyPolicyUri: String
}
