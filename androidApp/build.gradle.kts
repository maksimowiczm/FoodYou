plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    dependencies {
        implementation(projects.app)

        implementation(libs.androidx.activityCompose)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.datastore.preferencesCore)
        implementation(libs.compose.components.resources)
        implementation(libs.compose.material3)
        implementation(libs.compose.runtime)
        implementation(libs.insert.koin.android)
        implementation(libs.jetbrains.androidx.lifecycle.runtimeCompose)
        implementation(libs.touchlab.kermit)
        implementation(libs.vinceglb.filekitDialogsCompose)
    }
}

android {
    namespace = "com.maksimowiczm.foodyou"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        // TODO: update applicationId when release ready
        applicationId = "com.maksimowiczm.foodyou4"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.android.versionCode.get().toInt()
        versionName = libs.versions.version.name.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildFeatures {
            buildConfig = true
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
        }
    }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

    buildTypes {
        release {
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
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
