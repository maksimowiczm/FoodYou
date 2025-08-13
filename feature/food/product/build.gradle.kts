plugins {
    alias(libs.plugins.feature)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets.all { languageSettings.enableLanguageFeature("WhenGuards") }

    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.product" }

    val xcfName = "feature:food:productKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.shared.barcodescanner)

        implementation(projects.business.shared)
        implementation(projects.business.settings)
        implementation(projects.business.food)

        implementation(projects.feature.food.shared)

        implementation(libs.navigation.compose)

        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
    }
}
