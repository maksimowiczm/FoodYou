import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    // Leave it here
    // Otherwise IDE will not recognize the project as a multiplatform project
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core3"))
        }

        androidMain.dependencies {
            implementation(libs.accompanist.permissions)
            implementation(libs.androidx.activity.compose)

            // Zxing
            implementation(libs.zxing.android.embedded)
        }
    }
}

android {
    namespace = "com.maksimowiczm.foodyou.feature.barcodescanner"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = true

            // Not sure why this is can't be consumer proguard file
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}
