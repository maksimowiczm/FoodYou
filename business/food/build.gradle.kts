plugins {
    alias(libs.plugins.business)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.business.food"
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    val xcfName = "business:foodKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.core.shared)
            implementation(libs.core.food)
            implementation(libs.core.food.search)

            implementation(libs.androidx.paging.common)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)

            // Compose runtime
            implementation(compose.runtime)
            implementation(compose.components.resources)
        }
        androidMain.dependencies { implementation(libs.ktor.client.okhttp) }
        iosMain.dependencies { implementation(libs.ktor.client.darwin) }
    }
}
