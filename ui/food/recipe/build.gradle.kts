plugins {
    alias(libs.plugins.ui)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.food.recipe" }

    val xcfName = "ui:food:recipeKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(libs.core.shared)
        implementation(libs.core.food)
        implementation(projects.business.shared)

        implementation(projects.ui.food.shared)

        implementation(libs.kotlinx.serialization.json)
        implementation(libs.navigation.compose)
        implementation(libs.compose.shimmer)
        implementation(libs.kotlinx.datetime)
    }
}
