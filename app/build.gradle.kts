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
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Leave it here
    // Otherwise IDE will not recognize the project as a multiplatform one
    jvm("desktop")

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.work.runtime)

            implementation(libs.androidx.activity.compose)

            implementation(libs.accompanist.permissions)

            implementation(libs.androidx.browser)
        }
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":core-ui"))
            implementation(project(":core-database"))
            implementation(project(":core-model"))
            implementation(project(":core-domain"))
            implementation(project(":feature:barcodescanner"))
            implementation(project(":feature:language"))
            implementation(project(":feature:calendar"))
            implementation(project(":feature:product"))
            implementation(project(":feature:measurement"))
            implementation(project(":feature:recipe"))
            implementation(project(":feature:meal"))
            implementation(project(":feature:goals"))

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Kotlinx
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // Room
            implementation(libs.androidx.room.runtime)

            // Reorderable list
            implementation(libs.reorderable)

            // CSV
            implementation(libs.kotlin.csv)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
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

    buildTypes {
        release {
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    listOf("kspAndroid").forEach {
        add(it, libs.androidx.room.compiler)
    }

    debugImplementation(compose.uiTooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.test.core.ktx)
}
