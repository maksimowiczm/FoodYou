plugins { alias(libs.plugins.ui) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.sponsor" }

    val xcfName = "ui:sponsorKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.opensource)
        implementation(projects.business.shared)
        implementation(libs.core.shared)
        implementation(libs.core.sponsorship)

        implementation(libs.compose.shimmer)
        implementation(libs.androidx.paging.common)
        implementation(libs.kotlinx.datetime)
    }
}
