plugins { alias(libs.plugins.feature) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("ContextParameters") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.settings.meal" }

    val xcfName = "feature3:settings:mealKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.fooddiary)
        implementation(libs.reorderable)
        implementation(libs.kotlinx.datetime)
    }
}
