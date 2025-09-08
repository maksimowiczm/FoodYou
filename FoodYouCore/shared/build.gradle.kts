plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinMultiplatformAndroid)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    group = "com.maksimowiczm.foodyou"

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.shared"
        compileSdk = 36
        minSdk = 26
    }

    val xcfName = "core:sharedKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(libs.kotlinx.datetime)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization.json)
    }
}
