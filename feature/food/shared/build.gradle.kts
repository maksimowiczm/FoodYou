plugins { alias(libs.plugins.feature) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.shared" }

    val xcfName = "feature:food:sharedKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.shared.barcodescanner)

        implementation(projects.business.shared)
        implementation(projects.business.settings)
        implementation(projects.business.food)

        implementation(libs.compose.shimmer)
        implementation(libs.androidx.paging.common)
    }
}
