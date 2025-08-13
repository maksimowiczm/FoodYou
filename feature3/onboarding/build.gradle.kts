plugins {
    alias(libs.plugins.feature)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.onboarding" }

    val xcfName = "feature3:onboardingKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.externaldatabase.swissfoodcompositiondatabase)
        implementation(projects.business.food)
        implementation(projects.business.shared)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.navigation.compose)
    }
}
