plugins { alias(libs.plugins.feature) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("ContextParameters") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.home" }

    val xcfName = "feature:homeKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(libs.compose.shimmer)
        implementation(libs.kotlinx.datetime)
        implementation(libs.reorderable)
    }
}
