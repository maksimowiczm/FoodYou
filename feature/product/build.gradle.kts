import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    packageName("com.maksimowiczm.foodyou.feature.product")
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
        "\"FoodYou/$versionName-opensource (https://github.com/maksimowiczm/FoodYou)\""
    )

    sourceSets.getByName("main") {
        buildConfigField(
            "String",
            "OPEN_FOOD_FACTS_URL",
            "\"https://world.openfoodfacts.org/\""
        )
    }

    sourceSets.getByName("test") {
        buildConfigField(
            "String",
            "OPEN_FOOD_FACTS_URL",
            "\"https://world.openfoodfacts.net/\""
        )
    }

    buildConfigField(
        "String",
        "USDA_URL",
        "\"https://api.nal.usda.gov\""
    )
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.feature.product"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
                )
            }
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

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":core-ui"))
            implementation(project(":core-model"))
            implementation(project(":core-domain"))
            implementation(project(":core-database"))
            implementation(project(":feature:barcodescanner"))

            implementation(libs.kotlinx.serialization.json)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {

            // Ktor
            implementation(libs.ktor.client.okhttp)

            implementation(libs.androidx.browser)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
            implementation(libs.androidx.ui.test.manifest)
        }
    }
}
