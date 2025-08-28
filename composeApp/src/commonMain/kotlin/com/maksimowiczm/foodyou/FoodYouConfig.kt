package com.maksimowiczm.foodyou

import com.maksimowiczm.foodyou.business.shared.application.config.AppConfig

internal class FoodYouConfig : AppConfig {
    override val contactEmail: String = BuildConfig.FEEDBACK_EMAIL
    override val contactEmailUri: String = BuildConfig.FEEDBACK_EMAIL_URI
    override val versionName: String = BuildConfig.VERSION_NAME
    override val sourceCodeUrl: String = BuildConfig.GITHUB_URL
    override val issueTrackerUrl: String = BuildConfig.GITHUB_ISSUES_URL
    override val featureRequestUrl: String = BuildConfig.GITHUB_ISSUES_URL
    override val translationUrl: String = BuildConfig.CROWDIN_URL
}
