plugins { alias(libs.plugins.feature) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.about.sponsor" }

    val xcfName = "feature:about:sponsorKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.sponsorship)
        implementation(libs.compose.shimmer)
        implementation(libs.androidx.paging.common)
        implementation(libs.kotlinx.datetime)
    }
}
