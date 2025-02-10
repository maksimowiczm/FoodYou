plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.maksimowiczm.foodyou.core"
    compileSdk = libs.versions.coreCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.coreMinSdk.get().toInt()

        testOptions.targetSdk = libs.versions.coreTargetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.coreVersion.get()}\"")

        // -- OPEN FOOD FACTS --
        // https://openfoodfacts.github.io/openfoodfacts-server/api/#authentication
        // I know that this is in apk file, but it is not in git repository
        buildConfigField("String", "CONTACT_EMAIL", property("CONTACT_EMAIL").toString())

        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.org/\"")
        // Use cached open food facts data for development
        // buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"<cache-address>\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

            buildFeatures {
                viewBinding = true
            }
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

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.accompanist.permissions)
    "opensourceImplementation"(libs.zxing.android.embedded)

    implementation(libs.kotlin.result)

    // Retrofit
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.retrofit)
    implementation(libs.okhttp)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.datastore.preferences)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.foundation)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    ktlintRuleset(libs.ktlint.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
