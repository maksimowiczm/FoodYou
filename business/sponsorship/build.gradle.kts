plugins { alias(libs.plugins.business) }

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.business.sponsorship" }

    val xcfName = "business:sponsorshipKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.core.shared)
            implementation(libs.core.sponsorship)

            implementation(libs.androidx.paging.common)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.kotlinx.json)
        }

        androidMain.dependencies { implementation(libs.ktor.client.okhttp) }

        iosMain.dependencies { implementation(libs.ktor.client.darwin) }
    }
}
