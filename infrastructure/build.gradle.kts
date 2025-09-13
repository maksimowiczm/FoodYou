plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.gmazzo.buildconfig)
    alias(libs.plugins.kotlin.serialization)
}

buildConfig {
    packageName("com.maksimowiczm.foodyou.app.infrastructure")

    val sponsorApiUrl = "https://sponsors.foodyou.maksimowiczm.com"
    buildConfigField("String", "SPONSOR_API_URL", "\"$sponsorApiUrl\"")

    // -- OPEN FOOD FACTS --
    sourceSets.getByName("main") {
        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.org\"")
    }
    sourceSets.getByName("test") {
        buildConfigField("String", "OPEN_FOOD_FACTS_URL", "\"https://world.openfoodfacts.net\"")
    }

    // -- USDA --
    buildConfigField("String", "USDA_URL", "\"https://api.nal.usda.gov\"")
}

room { schemaDirectory("$projectDir/schemas") }

kotlin {
    sourceSets.all {
        languageSettings.enableLanguageFeature("ExpectActualClasses")
        languageSettings.enableLanguageFeature("WhenGuards")
    }

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.app.infrastructure"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}

        withDeviceTestBuilder { sourceSetTreeName = "test" }
            .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    val xcfName = "infrastructureKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.core.shared)
            implementation(libs.core.food)
            implementation(libs.core.fooddiary)
            implementation(libs.core.sponsorship)
            implementation(libs.core.goals)

            implementation(projects.business.opensource)
            implementation(projects.business.shared)

            implementation(projects.shared.common)

            // Kotlinx
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            // Koin
            implementation(libs.koin.core)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            // Datastore
            implementation(libs.androidx.datastore.preferences.core)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)

            // Compose runtime
            implementation(compose.runtime)
            implementation(compose.components.resources)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.room.testing)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.datastore.preferences.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.koin.android)
            implementation(libs.sqlite.android)
            implementation(libs.ktor.client.okhttp)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.testCore)
            implementation(libs.androidx.testCore.ktx)
            implementation(libs.androidx.testRunner)
            implementation(libs.androidx.testExt.junit)
        }

        iosMain.dependencies { implementation(libs.ktor.client.darwin) }
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
