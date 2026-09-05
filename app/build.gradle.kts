import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

room3 { schemaDirectory("$projectDir/schemas") }

val localProperties by lazy {
    Properties().apply {
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { load(it) }
        }
    }
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true

        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIn.add("kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xreturn-value-checker=check")
    }

    android {
        namespace = "com.maksimowiczm.foodyou.app"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

        androidResources.enable = true

        withHostTest {}
        withDeviceTestBuilder { sourceSetTreeName = "test" }
            .configure {
                localProperties.getProperty("usda.api.key")?.let {
                    instrumentationRunnerArguments["usda.api.key"] = it
                }
            }
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "App"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.google.accompanistPermissions)
            implementation(libs.google.zxing.core)
        }

        commonMain.dependencies {
            implementation(libs.androidx.datastore.preferencesCore)
            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.room.runtime)
            implementation(libs.calvin.reorderable)
            implementation(libs.coil.compose)
            implementation(libs.coil.networkKtor3)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.iconsExtended)
            implementation(libs.compose.material3)
            implementation(libs.compose.navigation3.ui)
            implementation(libs.compose.navigationevent.compose)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.toolingPreview)
            implementation(libs.insert.koin.compose)
            implementation(libs.insert.koin.composeViewmodel)
            implementation(libs.jetbrains.androidx.lifecycle.viewmodelNavigation3)
            implementation(libs.konform)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.materialKolor)
            implementation(libs.skydoves.colorpickerCompose)
            implementation(libs.touchlab.kermit)
            implementation(libs.valentinilk.shimmer.composeShimmer)
            implementation(libs.vinceglb.filekitCoil)
            implementation(libs.vinceglb.filekitDialogsCompose)
            api(projects.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesTest)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.test.core)
            implementation(libs.androidx.test.coreKtx)
            implementation(libs.androidx.test.ext.junit)
            implementation(libs.androidx.test.runner)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)

    listOf("kspCommonMainMetadata", "kspAndroid", "kspIosArm64", "kspIosSimulatorArm64").forEach {
        add(it, libs.androidx.room.compiler)
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "foodyou.app.generated.resources"
    generateResClass = always
}
