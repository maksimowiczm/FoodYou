package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.maksimowiczm.foodyou.app.domain.AppConfig

private val defaultAppConfig =
    object : AppConfig {
        override val versionName: String = "Default version"
        override val privacyPolicyUri: String = "privacyPolicyUri"
        override val openFoodFactsTermsOfUseUri: String = "openFoodFactsTermsOfUseUri"
        override val openFoodFactsPrivacyPolicyUri: String = "openFoodFactsPrivacyPolicyUri"
        override val foodDataCentralPrivacyPolicyUri: String = "foodDataCentralPrivacyPolicyUri"
        override val sourceCodeUri: String = "sourceCodeUri"
        override val featureRequestUri: String = "featureRequestUri"
        override val emailContactUri: String = "emailContactUri"
        override val translateUri: String = "translateUri"
        override val changelogUri: String = "changelogUri"
    }

val LocalAppConfig = staticCompositionLocalOf<AppConfig> { defaultAppConfig }

@Composable
fun AppConfigProvider(energyFormatter: AppConfig, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppConfig provides energyFormatter) { content() }
}
