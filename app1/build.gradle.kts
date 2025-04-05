import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    packageName("com.maksimowiczm.foodyou")
    className("BuildConfig")

    val versionName = libs.versions.version.name.get()

    buildConfigField("String", "VERSION_NAME", "\"$versionName\"")

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
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Leave it here
    // Otherwise IDE won't mark android dependencies as error in common code
    // jvm("desktop")

    sourceSets {
        androidMain.dependencies {
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
            implementation(compose.runtime)
//            implementation(compose.foundation)
            implementation(libs.androidx.foundation)
//            implementation(compose.material3)
            implementation(libs.androidx.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
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

            // Reorderable list
            implementation(libs.reorderable)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

android {
    namespace = "com.maksimowiczm.foodyou"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.maksimowiczm.foodyou"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.android.versionCode.get().toInt()
        versionName = libs.versions.version.name.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Test minified version with debug signing config
            // signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
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

    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.test.core.ktx)
}
