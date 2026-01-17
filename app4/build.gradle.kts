import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

room { schemaDirectory("$projectDir/schemas") }

kotlin {
    sourceSets.all {
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
        optIn.add("kotlin.contracts.ExperimentalContracts")
    }

    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "App"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.reflect)

            implementation(projects.shared.resources)
            implementation(projects.shared.barcodescanner)

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material.icons.extended)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.jetbrains.compose.navigationevent.compose)

            implementation(libs.jetbrains.compose.navigation.compose)

            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            implementation(libs.material.kolor)

            implementation(libs.kermit)

            implementation(libs.reorderable)

            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)

            implementation(libs.compose.shimmer)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            implementation(libs.colorpicker.compose)

            implementation(libs.filekit.coil)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.koin.android)
            implementation(libs.sqlite.android)
            implementation(libs.ktor.client.okhttp)
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

dependencies {
    debugImplementation(libs.jetbrains.compose.ui.tooling)

    listOf("kspCommonMainMetadata", "kspAndroid", "kspIosArm64", "kspIosSimulatorArm64").forEach {
        add(it, libs.androidx.room.compiler)
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.maksimowiczm.foodyou.app.generated.resources"
    generateResClass = always
}
