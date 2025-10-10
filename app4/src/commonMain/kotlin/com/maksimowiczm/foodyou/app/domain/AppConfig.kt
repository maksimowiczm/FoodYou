package com.maksimowiczm.foodyou.app.domain

interface AppConfig {
    /** Current version name of the application (e.g. "1.0.0"). */
    val versionName: String

    /** URI to the Terms of Use document. */
    val termsOfUseUri: String

    /** URI to the Privacy Policy document. */
    val privacyPolicyUri: String
}
