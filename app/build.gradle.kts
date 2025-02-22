plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.maksimowiczm.foodyou"
    compileSdk = libs.versions.coreCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.maksimowiczm.foodyou"
        minSdk = libs.versions.coreMinSdk.get().toInt()
        targetSdk = libs.versions.coreTargetSdk.get().toInt()
        versionCode = 1
        versionName = libs.versions.coreVersion.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
    }
}

dependencies {
    implementation(project(":core"))
}
