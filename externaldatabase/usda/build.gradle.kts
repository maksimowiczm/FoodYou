plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
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
        "\"FoodYou/$versionName (https://github.com/maksimowiczm/FoodYou)\"",
    )

    buildConfigField("String", "USDA_URL", "\"https://api.nal.usda.gov\"")
}

kotlin {
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.externaldatabase.usda"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}

        withDeviceTestBuilder { sourceSetTreeName = "test" }
            .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    }

    val xcfName = "externaldatabase:usdaKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:common"))
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)
        }

        commonTest.dependencies { implementation(libs.kotlin.test) }

        androidMain.dependencies {

            // Ktor
            implementation(libs.ktor.client.okhttp)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.test.core)
            implementation(libs.androidx.junit)
        }

        iosMain.dependencies {

            // Ktor
            implementation(libs.ktor.client.darwin)
        }
    }
}
