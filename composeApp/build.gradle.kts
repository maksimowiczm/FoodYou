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

    sourceSets.all {
        languageSettings.enableLanguageFeature("ContextParameters")
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)

            implementation(libs.sqlite.android)
            implementation(libs.koin.android)
        }

        commonMain.dependencies {

            implementation(projects.shared.common)
            implementation(projects.shared.ui)

            implementation(projects.navigation)

            implementation(projects.business.shared)
            implementation(projects.business.food)
            implementation(projects.business.fooddiary)
            implementation(projects.business.sponsorship)
            implementation(projects.business.settings)

            implementation(projects.feature3.shared)
            implementation(projects.feature3.about.master)
            implementation(projects.feature3.about.sponsor)

            implementation(projects.feature.openfoodfacts)
            implementation(projects.feature.usda)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            implementation(libs.androidx.datastore.preferences)

            implementation(compose.runtime)
            implementation(compose.foundation)
            // implementation(compose.material3)
            implementation(libs.jetbrains.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.navigation.compose)

            implementation(libs.koin.compose)

            implementation(libs.reorderable)

//            implementation(project(":core"))
//            implementation(project(":feature:about"))
//            implementation(project(":feature:food"))
//            implementation(project(":feature:measurement"))
//            implementation(project(":feature:fooddiary"))
            implementation(project(":feature:calendar"))
//            implementation(project(":feature:language"))
//            implementation(project(":feature:onboarding"))
//            implementation(project(":feature:importexport"))
//            implementation(project(":feature:swissfoodcompositiondatabase"))

            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.room.testing)
            implementation(libs.androidx.sqlite.bundled)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.core.ktx)
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
        create("preview") {
            initWith(getByName("release"))

            applicationIdSuffix = ".preview"
            versionNameSuffix = "-preview"
            manifestPlaceholders["applicationIcon"] = "@mipmap/ic_launcher_preview"
            manifestPlaceholders["applicationRoundIcon"] = "@mipmap/ic_launcher_round_preview"
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    listOf(
        "kspCommonMainMetadata",
        "kspAndroid",
        "kspIosX64",
        "kspIosArm64",
        "kspIosSimulatorArm64"
    ).forEach {
        add(it, libs.androidx.room.compiler)
    }
}
