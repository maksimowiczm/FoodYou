plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    sourceSets.all {
        languageSettings.enableLanguageFeature("ContextParameters")
        languageSettings.enableLanguageFeature("WhenGuards")
    }

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.shared.compose"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    val xcfName = "shared:composeKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        // implementation(compose.material3)
        implementation(libs.jetbrains.compose.material3)
        implementation(compose.materialIconsExtended)
        implementation(compose.ui)
        implementation(compose.components.resources)
        implementation(libs.navigation.compose)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.datetime)

        implementation(libs.androidx.paging.common)
        implementation(libs.reorderable)
    }
}
