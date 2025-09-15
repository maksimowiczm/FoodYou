plugins { alias(libs.plugins.ui) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("ContextParameters") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.meal" }

    val xcfName = "ui:mealKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(libs.core.shared)
        implementation(libs.core.fooddiary)

        implementation(libs.kotlinx.datetime)
        implementation(libs.reorderable)
    }
}
