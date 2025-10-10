package com.maksimowiczm.foodyou.app.domain

interface AppConfig {
    /** Current version name of the application (e.g. "1.0.0"). */
    val versionName: String

    /** URI to the Terms of Use document. */
    val termsOfUseUri: String

    /** URI to the Privacy Policy document. */
    val privacyPolicyUri: String

    /** URI to the Open Food Facts Terms of Use document. */
    val openFoodFactsTermsOfUseUri: String

    /** URI to the Open Food Facts Privacy Policy document. */
    val openFoodFactsPrivacyPolicyUri: String

    /** URI to the FoodData Central Privacy Policy document. */
    val foodDataCentralPrivacyPolicyUri: String

    /** URI to the source code repository. */
    val sourceCodeUri: String

    /** URI to the feature request page. */
    val featureRequestUri: String

    /** URI to the email contact. */
    val emailContactUri: String

    /** URI to the translation platform. */
    val translateUri: String
}
