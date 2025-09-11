package com.maksimowiczm.foodyou.app.business.opensource.domain.config

interface AppConfig {
    val contactEmail: String
    val contactEmailUri: String
    val versionName: String
    val sourceCodeUrl: String
    val issueTrackerUrl: String
    val featureRequestUrl: String
    val translationUrl: String
}
