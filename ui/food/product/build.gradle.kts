plugins {
    alias(libs.plugins.ui)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.food.product" }

    val xcfName = "ui:food:productKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(libs.core.shared)
        implementation(libs.core.food)

        implementation(projects.ui.food.shared)

        implementation(libs.kotlinx.serialization.json)
        implementation(libs.navigation.compose)
        implementation(libs.compose.shimmer)
        implementation(libs.kotlinx.datetime)
    }
}
