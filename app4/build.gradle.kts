import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
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

            implementation(compose.runtime)
            implementation(compose.foundation)
            // implementation(compose.material3)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.backhandler)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.navigation.compose)

            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies { implementation(libs.kotlin.test) }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.koin.android)
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
        applicationId = "com.maksimowiczm.foodyou4"
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

dependencies { debugImplementation(compose.uiTooling) }

compose.resources {
    publicResClass = true
    packageOfResClass = "com.maksimowiczm.foodyou.app.generated.resources"
    generateResClass = always
}
