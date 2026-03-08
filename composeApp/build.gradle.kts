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

room { schemaDirectory("$projectDir/schemas") }

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.ui.ExperimentalComposeUiApi")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        optIn.add("kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xreturn-value-checker=check")
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    android {
        namespace = "com.maksimowiczm.foodyoulibrary"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

        androidResources.enable = true

        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder { sourceSetTreeName = "test" }

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.files.add(project.file("proguard-rules.pro"))
        }
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.bundles.camera)
            implementation(libs.google.accompanistPermissions)
            implementation(libs.google.zxing.core)
            implementation(libs.ktor.clientOkhttp)
            implementation(libs.requery.sqliteAndroid)
        }

        commonMain.dependencies {
            implementation(libs.androidx.datastore.preferencesCore)
            implementation(libs.bundles.coil)
            implementation(libs.bundles.compose.core)
            implementation(libs.bundles.compose.navigation)
            implementation(libs.bundles.filekit)
            implementation(libs.bundles.koin.compose)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.paging)
            implementation(libs.bundles.room)
            implementation(libs.bundles.ktor)
            implementation(libs.calvin.reorderable)
            implementation(libs.kotlin.reflect)
            implementation(libs.konform)
            implementation(libs.materialKolor)
            implementation(libs.skydoves.colorpickerCompose)
            implementation(libs.touchlab.kermit)
            implementation(libs.valentinilk.shimmer.composeShimmer)
        }

        commonTest.dependencies {
            implementation(libs.androidx.room.testing)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesTest)
        }

        getByName("androidDeviceTest").dependencies { implementation(libs.bundles.androidx.test) }

        iosMain.dependencies { implementation(libs.ktor.clientDarwin) }
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
