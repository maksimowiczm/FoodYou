plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        // Allow multi-module compose resources
        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/whats-new-compose-180.html#support-for-multiplatform-resources-in-the-androidlibrary-target
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "core3Kit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain.dependencies {

            // Compose
            api(compose.runtime)
            api(compose.foundation)
            // implementation(compose.material3)
            api(libs.androidx.material3)
            api(compose.materialIconsExtended)
            api(compose.ui)
            api(compose.components.resources)
            api(libs.navigation.compose)

            // DataStore
            api(libs.androidx.datastore.preferences)

            // Datetime
            api(libs.kotlinx.datetime)

            // Paging
            api(libs.androidx.paging.runtime)

            implementation(kotlin("reflect"))

            // Koin
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)

            // Logger
            api(libs.kermit)

            // Shimmer
            api(libs.compose.shimmer)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            api(libs.koin.android)
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain.dependencies {
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "foodyou.app.generated.resources"
    generateResClass = always
}
