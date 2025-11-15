package com.maksimowiczm.foodyou.common.config

interface AppConfig {
    /** Email address for contacting the developer. */
    val contactEmail: String

    /** Mailto URI for contacting the developer (including subject and body). */
    val contactEmailUri: String

    /** Current version name of the application (e.g. "1.0.0"). */
    val versionName: String

    /** URL to the translation platform where users can contribute translations. */
    val translationUrl: String

    val sourceCodeUrl: String
    val issueTrackerUrl: String
    val featureRequestUrl: String

    val termsOfServiceUrl: String
    val privacyPolicyUrl: String

    /** URI to the Open Food Facts Terms of Use document. */
    val openFoodFactsTermsOfUseUri: String

    /** URI to the Open Food Facts Privacy Policy document. */
    val openFoodFactsPrivacyPolicyUri: String

    /** URI to the FoodData Central Privacy Policy document. */
    val foodDataCentralPrivacyPolicyUri: String
}
