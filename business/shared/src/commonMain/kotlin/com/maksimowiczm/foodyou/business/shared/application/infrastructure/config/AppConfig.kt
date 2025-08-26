package com.maksimowiczm.foodyou.business.shared.application.infrastructure.config

interface AppConfig {
    val contactEmail: String
    val contactEmailUri: String
    val versionName: String
    val sourceCodeUrl: String
    val issueTrackerUrl: String
    val featureRequestUrl: String
    val translationUrl: String
}
