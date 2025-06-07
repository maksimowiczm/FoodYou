plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
                )
            }
        }

        // Allow multi-module compose resources
        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/whats-new-compose-180.html#support-for-multiplatform-resources-in-the-androidlibrary-target
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":core-ui"))
            implementation(project(":feature:importexport"))

            implementation(compose.components.resources)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
        }
    }
}
