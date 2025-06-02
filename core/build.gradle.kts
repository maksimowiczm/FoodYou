plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
                )
            }
        }
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
            api(compose.components.resources)
            api(libs.navigation.compose)

            // Kotlinx
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.coroutines.core)

            // Logger
            api(libs.kermit)

            // Koin
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)

            // Datastore
            api(libs.androidx.datastore.preferences)

            // Shimmer
            api(libs.compose.shimmer)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            api(libs.androidx.appcompat)

            // Koin
            api(libs.koin.android)
            api(libs.koin.androidx.compose)
        }
    }
}

// Somehow compose resources are not working in multi module project but if they are defined in the
// app module they work fine. WEIRD
// Use symlink to the app module resources directory to make it work.
compose.resources {
    publicResClass = true
    packageOfResClass = "foodyou.app.generated.resources"
    generateResClass = auto
}
