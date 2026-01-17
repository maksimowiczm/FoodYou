package com.maksimowiczm.foodyou.app.domain

fun testAppConfig(
    versionName: String = "testVersionName",
    privacyPolicyUri: String = "testPrivacyPolicyUri",
    openFoodFactsTermsOfUseUri: String = "testOpenFoodFactsTermsOfUseUri",
    openFoodFactsPrivacyPolicyUri: String = "testOpenFoodFactsPrivacyPolicyUri",
    foodDataCentralPrivacyPolicyUri: String = "testFoodDataCentralPrivacyPolicyUri",
    sourceCodeUri: String = "testSourceCodeUri",
    featureRequestUri: String = "testFeatureRequestUri",
    emailContactUri: String = "testEmailContactUri",
    translateUri: String = "testTranslateUri",
    changelogUri: String = "testChangelogUri",
): AppConfig =
    object : AppConfig {
        override val versionName: String = versionName
        override val privacyPolicyUri: String = privacyPolicyUri
        override val openFoodFactsTermsOfUseUri: String = openFoodFactsTermsOfUseUri
        override val openFoodFactsPrivacyPolicyUri: String = openFoodFactsPrivacyPolicyUri
        override val foodDataCentralPrivacyPolicyUri: String = foodDataCentralPrivacyPolicyUri
        override val sourceCodeUri: String = sourceCodeUri
        override val featureRequestUri: String = featureRequestUri
        override val emailContactUri: String = emailContactUri
        override val translateUri: String = translateUri
        override val changelogUri: String = changelogUri
    }
