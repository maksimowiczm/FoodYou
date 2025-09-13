plugins { alias(libs.plugins.ui) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.theme" }

    val xcfName = "ui:themeKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.opensource)
        implementation(projects.business.shared)
        implementation(libs.core.shared)
        implementation(libs.material.kolor)
    }
}
