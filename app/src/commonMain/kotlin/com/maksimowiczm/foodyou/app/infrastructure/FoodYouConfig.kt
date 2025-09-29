package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.common.config.AppConfig

internal class FoodYouConfig : AppConfig {
    override val contactEmail: String = BuildConfig.FEEDBACK_EMAIL
    override val contactEmailUri: String = BuildConfig.FEEDBACK_EMAIL_URI
    override val versionName: String = BuildConfig.VERSION_NAME
    override val translationUrl: String = BuildConfig.CROWDIN_URL
    override val sourceCodeUrl: String = BuildConfig.GITHUB_URL
    override val issueTrackerUrl: String = BuildConfig.GITHUB_ISSUES_URL
    override val featureRequestUrl: String = BuildConfig.GITHUB_ISSUES_URL
    override val termsOfServiceUrl: String = BuildConfig.TERMS_OF_SERVICE_URL
    override val privacyPolicyUrl: String = BuildConfig.PRIVACY_POLICY_URL
}
