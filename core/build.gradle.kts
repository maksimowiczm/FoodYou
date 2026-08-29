import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

room3 { schemaDirectory("$projectDir/schemas") }

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIn.add("kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xreturn-value-checker=check")
    }

    android {
        namespace = "com.maksimowiczm.foodyou.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

        withHostTestBuilder {}.configure {}

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.files.add(project.file("proguard-rules.pro"))
        }
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Core"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.datastore.preferencesCore)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.room.runtime)
            implementation(libs.insert.koin.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.serializationKotlinxJson)
            implementation(libs.touchlab.kermit)
            implementation(libs.vinceglb.filekitCore)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesTest)
        }

        androidMain.dependencies {
            implementation(libs.insert.koin.android)
            implementation(libs.ktor.clientOkhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.clientDarwin)
        }
    }
}

dependencies {
    listOf("kspCommonMainMetadata", "kspAndroid", "kspIosArm64", "kspIosSimulatorArm64").forEach {
        add(it, libs.androidx.room.compiler)
    }
}
