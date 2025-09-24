plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.resources"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    val xcfName = "resourcesKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(compose.runtime)
        implementation(compose.components.resources)
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "foodyou.app.generated.resources"
    generateResClass = always
}
