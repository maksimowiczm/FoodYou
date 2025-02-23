plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.maksimowiczm.foodyou"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.maksimowiczm.foodyou"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // -- OPEN FOOD FACTS --
        // https://openfoodfacts.github.io/openfoodfacts-server/api/#authentication
        // I know that this is in apk file, but it is not in git repository
        buildConfigField("String", "CONTACT_EMAIL", property("CONTACT_EMAIL").toString())

        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.net/\"")
        // Use cached open food facts data for development
        // buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"<cache-address>\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xwhen-guards"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "version"

    productFlavors {
        /**
         * Open source flavour. This flavour is used for the open source version of the app. It
         * allows only the open source dependencies to be used.
         */
        create("opensource") {
            dimension = "version"
            isDefault = true

            versionNameSuffix = "-opensource"

            buildFeatures {
                viewBinding = true
            }
        }

        /**
         * Use this flavour to verify that core can be compiled.
         */
        create("dummy") {
            dimension = "version"

            versionNameSuffix = "-dummy"
        }
    }

    sourceSets {
        getByName("opensource") {
            res.srcDirs("src/opensource/res")
            java.srcDirs("src/opensource/java")
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Shimmer
    implementation(libs.compose.shimmer)

    // Coil
    "opensourceImplementation"(libs.coil.compose)
    "opensourceImplementation"(libs.coil.network.okhttp)

    implementation(libs.accompanist.permissions)

    // Zxing
    "opensourceImplementation"(libs.zxing.android.embedded)

    // Utils
    implementation(libs.kotlin.result)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Retrofit
    "opensourceImplementation"(libs.converter.kotlinx.serialization)
    "opensourceImplementation"(libs.retrofit)
    "opensourceImplementation"(libs.okhttp)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Room
    "opensourceImplementation"(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    "opensourceImplementation"(libs.androidx.room.ktx)
    "opensourceImplementation"(libs.androidx.room.paging)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    // Androidx compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
}
