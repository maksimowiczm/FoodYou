import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.accompanist.permissions)

            // Zxing
            implementation(libs.zxing.android.embedded)

            // Koin
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            // Ktor
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(compose.preview)

            implementation(compose.runtime)
            implementation(compose.foundation)
//            implementation(compose.material3)
            implementation(libs.androidx.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlin.result)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.navigation.compose)

            implementation(libs.kotlinx.datetime)

            // Shimmer
            implementation(libs.compose.shimmer)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.okhttp)

            // Datastore
            implementation(libs.androidx.datastore.preferences)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)

            implementation(libs.androidx.paging.runtime)

            // Logger
            implementation(libs.kermit)
        }
    }
}

android {
    namespace = "com.maksimowiczm.foodyou"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.maksimowiczm.foodyou"
        minSdk = 28
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // -- OPEN FOOD FACTS --
        // https://openfoodfacts.github.io/openfoodfacts-server/api/#authentication
        // Sorry no email 😭
        // https://pub.dev/packages/openfoodfacts#migrating-from-2xx-to-3xx-breaking-changes
        // https://pub.dev/packages/openfoodfacts#setup-optional
        buildConfigField(
            "String",
            "OPEN_FOOD_FACTS_USER_AGENT",
            "\"FoodYou/$versionName-opensource (https://github.com/maksimowiczm/FoodYou)\""
        )
    }

    android {
        androidResources {
            generateLocaleConfig = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "OPEN_FOOD_FACTS_URL",
                "\"https://world.openfoodfacts.org/\""
            )

            // Test minified version with debug signing config
            // signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            buildConfigField(
                "String",
                "OPEN_FOOD_FACTS_URL",
                "\"https://world.openfoodfacts.net/\""
            )

            // Use cached open food facts data for development. See
            // dev/open-food-facts-cache directory for more information
            // buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"<cache-address>\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(compose.uiTooling)
    listOf("kspAndroid").forEach {
        add(it, libs.androidx.room.compiler)
    }
}
