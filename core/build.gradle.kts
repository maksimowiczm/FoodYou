plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
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
            // Kotlinx
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.coroutines.core)

            // Logger
            api(libs.kermit)

            // Koin
            api(libs.koin.core)

            // Datastore
            api(libs.androidx.datastore.preferences)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            api(libs.androidx.appcompat)

            // Koin
            api(libs.koin.android)
        }
    }
}
