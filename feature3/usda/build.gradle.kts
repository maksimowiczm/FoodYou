plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    packageName("com.maksimowiczm.foodyou.feature.usda")
    className("BuildConfig")

    val versionName = libs.versions.version.name.get()

    buildConfigField(
        "String",
        "USER_AGENT",
        "\"FoodYou/$versionName (https://github.com/maksimowiczm/FoodYou)\""
    )

    buildConfigField(
        "String",
        "USDA_URL",
        "\"https://api.nal.usda.gov\""
    )
}
kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.feature.usda"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "feature3:usdaKit"

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
            implementation(project(":core3"))

            implementation(libs.kotlinx.serialization.json)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {

            // Ktor
            implementation(libs.ktor.client.okhttp)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
        }

        iosMain.dependencies {

            // Ktor
            implementation(libs.ktor.client.darwin)
        }
    }
}
