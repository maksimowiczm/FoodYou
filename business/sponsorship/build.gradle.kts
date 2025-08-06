plugins {
    alias(libs.plugins.business)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    val versionName = libs.versions.version.name.get()

    val feedbackEmail = "maksimowicz.dev@gmail.com"
    val feedbackEmailUri =
        "mailto:$feedbackEmail?subject=Food You Feedback&body=Food You Version: $versionName\\n"

    buildConfigField("String", "FEEDBACK_EMAIL_URI", "\"$feedbackEmailUri\"")
    buildConfigField("String", "VERSION_NAME", "\"$versionName\"")

    val sponsorApiUrl = "https://sponsors.foodyou.maksimowiczm.com"
    buildConfigField("String", "SPONSOR_API_URL", "\"$sponsorApiUrl\"")

    val sponsorApiUserAgent = "Food You/$versionName"
    buildConfigField("String", "SPONSOR_API_USER_AGENT", "\"$sponsorApiUserAgent\"")
}

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.business.sponsorship" }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "business:sponsorshipKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
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
