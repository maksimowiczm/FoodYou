plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    val versionName = libs.versions.version.name.get()

    val feedbackEmail = "maksimowicz.dev@gmail.com"
    val feedbackEmailUri =
        "mailto:$feedbackEmail?subject=Food You Feedback&body=Food You Version: $versionName\\n"

    buildConfigField("String", "FEEDBACK_EMAIL_URI", "\"$feedbackEmailUri\"")
    buildConfigField("String", "VERSION_NAME", "\"$versionName\"")

    val sponsorApiUrl = "https://sponsors.foodyou.maksimowiczm.com"
    buildConfigField("String", "SPONSOR_API_URL", "\"$sponsorApiUrl\"")

    val sponsorApiUserAgent = "Food You/$versionName"
    buildConfigField("String", "SPONSOR_API_USER_AGENT", "\"$sponsorApiUserAgent\"")
}

kotlin {

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.feature.about"
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
    val xcfName = "feature3:aboutKit"

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

            implementation(libs.androidx.paging.runtime)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            implementation(libs.kotlinx.serialization.json)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)

            implementation(libs.compose.shimmer)
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
            implementation(libs.ktor.client.darwin)
        }
    }
}
