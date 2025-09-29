import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

room { schemaDirectory("$projectDir/schemas") }

buildConfig {
    packageName("com.maksimowiczm.foodyou.app")
    className("BuildConfig")

    val versionName = libs.versions.version.name.get()
    buildConfigField("String", "VERSION_NAME", "\"$versionName\"")

    val feedbackEmail = "maksimowicz.dev@gmail.com"
    buildConfigField("String", "FEEDBACK_EMAIL", "\"$feedbackEmail\"")

    val feedbackEmailUri =
        "mailto:$feedbackEmail?subject=Food You Feedback&body=Food You Version: $versionName\\n"
    buildConfigField("String", "FEEDBACK_EMAIL_URI", "\"$feedbackEmailUri\"")

    val githubUrl = "https://github.com/maksimowiczm/FoodYou"
    val githubIssues = "$githubUrl/issues"
    buildConfigField("String", "GITHUB_URL", "\"$githubUrl\"")
    buildConfigField("String", "GITHUB_ISSUES_URL", "\"$githubIssues\"")

    val crowdin = "https://crowdin.com/project/food-you"
    buildConfigField("String", "CROWDIN_URL", "\"$crowdin\"")

    val termsOfService = "TODO"
    buildConfigField("String", "TERMS_OF_SERVICE_URL", "\"$termsOfService\"")
    val privacyPolicy = "TODO"
    buildConfigField("String", "PRIVACY_POLICY_URL", "\"$privacyPolicy\"")

    // -- OPEN FOOD FACTS --
    sourceSets.getByName("main") {
        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.org\"")
    }
    sourceSets.getByName("test") {
        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.net\"")
    }

    // -- USDA --
    buildConfigField("String", "USDA_URL", "\"https://api.nal.usda.gov\"")

    // -- Food You Sponsors github repository --
    buildConfigField(
        "String",
        "GITHUB_SPONSORS_REPOSITORY_URL",
        "\"https://maksimowiczm.github.io/FoodYou-sponsors\"",
    )
}

kotlin {
    sourceSets.all {
        languageSettings.enableLanguageFeature("WhenGuards")
        languageSettings.enableLanguageFeature("ExpectActualClasses")
        languageSettings.enableLanguageFeature("ContextParameters")
    }

    compilerOptions {
        optIn.add("androidx.compose.ui.ExperimentalComposeUiApi")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "App"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.common)
            implementation(projects.shared.resources)
            implementation(projects.shared.barcodescanner)

            implementation(compose.runtime)
            implementation(compose.foundation)
            // implementation(compose.material3)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.backhandler)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.navigation.compose)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.material.kolor)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)

            implementation(libs.androidx.paging.common)

            implementation(libs.reorderable)

            implementation(libs.compose.shimmer)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.room.testing)
            implementation(libs.androidx.sqlite.bundled)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqlite.android)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.testCore)
            implementation(libs.androidx.testCore.ktx)
            implementation(libs.androidx.testRunner)
            implementation(libs.androidx.testExt.junit)
        }

        iosMain.dependencies { implementation(libs.ktor.client.darwin) }
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

        manifestPlaceholders["applicationIcon"] = "@mipmap/ic_launcher"
        manifestPlaceholders["applicationRoundIcon"] = "@mipmap/ic_launcher_round"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        create("devRelease") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        create("miniDevRelease") {
            initWith(getByName("devRelease"))
            isMinifyEnabled = true
        }
        create("preview") {
            initWith(getByName("release"))

            applicationIdSuffix = ".preview"
            versionNameSuffix = "-preview"
            manifestPlaceholders["applicationIcon"] = "@mipmap/ic_launcher_preview"
            manifestPlaceholders["applicationRoundIcon"] = "@mipmap/ic_launcher_round_preview"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    listOf(
            "kspCommonMainMetadata",
            "kspAndroid",
            "kspIosX64",
            "kspIosArm64",
            "kspIosSimulatorArm64",
        )
        .forEach { add(it, libs.androidx.room.compiler) }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.maksimowiczm.foodyou.app.generated.resources"
    generateResClass = always
}
