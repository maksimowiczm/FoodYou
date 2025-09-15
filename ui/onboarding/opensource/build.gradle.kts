plugins {
    alias(libs.plugins.ui)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.onboarding.opensource" }

    val xcfName = "ui:onboarding:opensourceKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(projects.business.opensource)
        implementation(libs.core.shared)

        implementation(libs.kotlinx.serialization.json)
        implementation(libs.navigation.compose)
    }
}
