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
}

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("ContextParameters") }

    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(libs.core.shared)
            implementation(libs.core.sponsorship)

            implementation(projects.shared.common)
            implementation(projects.shared.ui)
            implementation(projects.shared.barcodescanner)

            implementation(projects.navigation)

            implementation(projects.business.shared)
            implementation(projects.business.food)
            implementation(projects.business.fooddiary)
            implementation(projects.business.sponsorship)
            implementation(projects.business.settings)

            implementation(projects.feature.shared)
            implementation(projects.feature.about.master)
            implementation(projects.feature.about.sponsor)
            implementation(projects.feature.settings.master)
            implementation(projects.feature.settings.language)
            implementation(projects.feature.settings.personalization)
            implementation(projects.feature.database.master)
            implementation(projects.feature.database.externaldatabases)
            implementation(projects.feature.database.databasedump)
            implementation(projects.feature.database.swissfoodcompositiondatabase)
            implementation(projects.feature.database.importcsvproducts)
            implementation(projects.feature.database.exportcsvproducts)
            implementation(projects.feature.home)
            implementation(projects.feature.goals)
            implementation(projects.feature.food.shared)
            implementation(projects.feature.food.diary.search)
            implementation(projects.feature.food.diary.add)
            implementation(projects.feature.food.product)
            implementation(projects.feature.food.recipe)
            implementation(projects.feature.food.diary.update)
            implementation(projects.feature.food.diary.shared)
            implementation(projects.feature.food.diary.meal)
            implementation(projects.feature.food.diary.quickadd)
            implementation(projects.feature.onboarding)

            implementation(compose.runtime)
            implementation(compose.foundation)
            // implementation(compose.material3)
            implementation(libs.jetbrains.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies { implementation(libs.kotlin.test) }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.testCore)
            implementation(libs.androidx.testCore.ktx)
            implementation(libs.androidx.testRunner)
            implementation(libs.androidx.testExt.junit)
        }
    }
}

room { schemaDirectory("$projectDir/schemas") }

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
