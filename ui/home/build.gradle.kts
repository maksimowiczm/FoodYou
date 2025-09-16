plugins { alias(libs.plugins.ui) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("ContextParameters") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.home" }

    val xcfName = "ui:homeKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(libs.core.shared)
        implementation(libs.core.fooddiary)
        implementation(libs.core.goals)

        implementation(libs.compose.shimmer)
        implementation(libs.kotlinx.datetime)
        implementation(libs.reorderable)
    }
}
