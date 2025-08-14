plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    packageName("com.maksimowiczm.foodyou.externaldatabase.openfoodfacts")
    className("BuildConfig")

    val versionName = libs.versions.version.name.get()

    // -- OPEN FOOD FACTS --
    // https://openfoodfacts.github.io/openfoodfacts-server/api/#authentication
    // Sorry no email 😭
    // https://pub.dev/packages/openfoodfacts#migrating-from-2xx-to-3xx-breaking-changes
    // https://pub.dev/packages/openfoodfacts#setup-optional
    buildConfigField(
        "String",
        "USER_AGENT",
        "\"FoodYou/$versionName (https://github.com/maksimowiczm/FoodYou)\"",
    )

    sourceSets.getByName("main") {
        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.org\"")
    }

    sourceSets.getByName("test") {
        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.net\"")
    }
}

kotlin {
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.externaldatabase.openfoodfacts"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}

        withDeviceTestBuilder { sourceSetTreeName = "test" }
            .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    }

    val xcfName = "externaldatabase:openfoodfactsKit"

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
