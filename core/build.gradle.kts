import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Leave it here
    // Otherwise IDE won't mark android dependencies as error in common code
    jvm("desktop")

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            api(compose.runtime)
            api(compose.foundation)
//            api(compose.material3)
            api(libs.androidx.material3)
            api(compose.materialIconsExtended)
            api(compose.ui)
            api(libs.navigation.compose)
            api(compose.components.resources)

            // Kotlinx
            api(libs.kotlinx.serialization.json)
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.coroutines.core)

            // Logger
            api(libs.kermit)

            // Koin
            api(libs.koin.core)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            // Datastore
            implementation(libs.androidx.datastore.preferences)

            // Shimmer
            implementation(libs.compose.shimmer)

            // Reorderable list
            implementation(libs.reorderable)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            api(libs.androidx.appcompat)
        }
    }
}

android {
    namespace = "com.maksimowiczm.foodyou.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "foodyou.app.generated.resources"
    generateResClass = auto
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
