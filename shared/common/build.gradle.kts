plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
}

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("ExpectActualClasses") }

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.shared.common"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    val xcfName = "shared:commonKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.core.shared)
        }

        commonTest { dependencies { implementation(libs.kotlin.test) } }

        androidMain.dependencies { implementation(libs.androidx.appcompat) }
    }
}
