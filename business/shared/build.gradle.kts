plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    val sponsorApiUrl = "https://sponsors.foodyou.maksimowiczm.com"
    buildConfigField("String", "SPONSOR_API_URL", "\"$sponsorApiUrl\"")
}

room { schemaDirectory("$projectDir/schemas") }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("ExpectActualClasses") }

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.business.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}

        withDeviceTestBuilder { sourceSetTreeName = "test" }
            .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    val xcfName = "business:sharedKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.common)
            implementation(libs.koin.core)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.room.testing)
            implementation(libs.androidx.sqlite.bundled)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.sqlite.android)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.test.core.ktx)
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
        }
    }
}

dependencies {
    listOf(
            "kspCommonMainMetadata",
            "kspAndroid",
            "kspIosX64",
            "kspIosArm64",
            "kspIosSimulatorArm64",
        )
        .forEach { add(it, libs.androidx.room.compiler) }
}
